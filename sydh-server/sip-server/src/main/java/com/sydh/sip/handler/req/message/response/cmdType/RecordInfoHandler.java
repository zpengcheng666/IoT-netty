package com.sydh.sip.handler.req.message.response.cmdType;

import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.handler.req.ReqAbstractHandler;
import com.sydh.sip.handler.req.message.IMessageHandler;
import com.sydh.sip.handler.req.message.response.ResponseMessageHandler;
import com.sydh.sip.model.RecordItem;
import com.sydh.sip.model.RecordList;
import com.sydh.sip.server.RecordCacheManager;
import com.sydh.sip.service.ISipCacheService;
import com.sydh.sip.util.SipUtil;
import com.sydh.sip.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class RecordInfoHandler extends ReqAbstractHandler implements InitializingBean, IMessageHandler {

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private ISipCacheService sipCacheService;

    @Autowired
    private RecordCacheManager recordCacheManager;

    @Override
    public void handlerCmdType(RequestEvent evt, SipDevice device, Element element) {
        ReentrantLock lock = null;
        try {
            // 回复200 OK
            responseAck(evt);
            Element rootElement = getRootElement(evt);
            if (rootElement == null) {
                log.error("根元素为空，无法处理请求");
                return;
            }
            String deviceId = rootElement.element("DeviceID").getText();
            String sn = XmlUtil.getText(rootElement, "SN");
            String sumNumStr = XmlUtil.getText(rootElement, "SumNum");
            String recordkey = String.format("%s:%s", deviceId, sn);
            lock = recordCacheManager.getlock(recordkey);
            try {
                // 尝试获取锁，如果锁不可用则等待
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                // 处理中断异常
                Thread.currentThread().interrupt(); // 恢复中断状态
                log.warn("lock 恢复中断状态: {}", e.getMessage());
            }
            int recordnum = 0;
            RecordList recordList = recordCacheManager.get(recordkey);
            recordList.setDeviceID(deviceId);
            Element recordListElement = rootElement.element("RecordList");
            if (recordListElement == null || sumNumStr == null || sumNumStr.isEmpty()){
                log.info("无录像数据");
            } else {
                Iterator<Element> recordListIterator = recordListElement.elementIterator();
                if (recordListIterator != null) {
                    while (recordListIterator.hasNext()) {
                        Element itemRecord = recordListIterator.next();
                        Element recordElement = itemRecord.element("DeviceID");
                        if (recordElement == null) {
                            continue;
                        }
                        RecordItem item = new RecordItem();
                        item.setStart(SipUtil.ISO8601Totimestamp(XmlUtil.getText(itemRecord, "StartTime")));
                        item.setEnd(SipUtil.ISO8601Totimestamp(XmlUtil.getText(itemRecord, "EndTime")));
                        recordList.addItem(item);
                        recordnum++;
                    }
                }
            }
            log.info("sn:{}, getSumNum:{}, recordnum:{}, sumNum:{}", sn, recordList.getSumNum(), recordnum, sumNumStr);
            int sumNum = Integer.parseInt(sumNumStr);
            if (recordList.getSumNum() + recordnum == sumNum) {
                recordList.mergeItems();
                log.info("mergeItems recordList:{}", recordList);
                recordCacheManager.remove(recordkey);
                sipCacheService.setRecordList(recordkey, recordList);
            } else {
                recordList.setSumNum(recordList.getSumNum() + recordnum);
                recordCacheManager.put(recordkey, recordList);
            }
        } catch (DocumentException | SipException | InvalidArgumentException | ParseException e) {
            log.error("处理请求时发生异常: {}", e.getMessage(), e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String cmdType = "RecordInfo";
        responseMessageHandler.addHandler(cmdType, this);
    }
}
