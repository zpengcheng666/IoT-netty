package com.sydh.iot.data.ruleEngine;

import com.sydh.common.extend.core.domin.thingsModel.SceneThingsModelItem;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.rule.cmp.data.ConditionData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@LiteflowComponent("condition")
public class ConditionCmp extends NodeBooleanComponent {

    @Override
    public boolean processBoolean() throws Exception {
        ConditionData data = this.getCmpData(ConditionData.class);
        SceneContext cxt = this.getContextBean(SceneContext.class);
        List<SceneThingsModelItem> sceneThingsModelItems = cxt.getSceneThingsModelItems();
        List<ThingsModelSimpleItem> thingsModelSimpleItems = cxt.getThingsModelSimpleItems();

        boolean result = false;
        String operator = data.getExpressions().get(0).getOperator();
        String value = data.getExpressions().get(0).getOperator();
        // 获取触发数据值
        String triggerValue = "";
        // 操作符比较
        switch (operator) {
            case "=":
                result = value.equals(triggerValue);
                break;
            case "!=":
                result = !value.equals(triggerValue);
                break;
            case ">":
                if (isNumeric(value) && isNumeric(triggerValue)) {
                    result = Double.parseDouble(value) > Double.parseDouble(triggerValue);
                }
                break;
            case "<":
                if (isNumeric(value) && isNumeric(triggerValue)) {
                    result = Double.parseDouble(value) < Double.parseDouble(triggerValue);
                }
                break;
            case ">=":
                if (isNumeric(value) && isNumeric(triggerValue)) {
                    result = Double.parseDouble(value) >= Double.parseDouble(triggerValue);
                }
                break;
            case "<=":
                if (isNumeric(value) && isNumeric(triggerValue)) {
                    result = Double.parseDouble(value) <= Double.parseDouble(triggerValue);
                }
                break;
            case "between":
                // 比较值用英文中划线分割 -
                String[] triggerValues = triggerValue.split("-");
                if (isNumeric(value) && isNumeric(triggerValues[0]) && isNumeric(triggerValues[1])) {
                    result = Double.parseDouble(value) >= Double.parseDouble(triggerValues[0]) && Double.parseDouble(value) <= Double.parseDouble(triggerValues[1]);
                }
                break;
            case "notBetween":
                // 比较值用英文中划线分割 -
                String[] trigValues = triggerValue.split("-");
                if (isNumeric(value) && isNumeric(trigValues[0]) && isNumeric(trigValues[1])) {
                    result = Double.parseDouble(value) <= Double.parseDouble(trigValues[0]) || Double.parseDouble(value) >= Double.parseDouble(trigValues[1]);
                }
                break;
            case "contain":
                result = value.contains(triggerValue);
                break;
            case "notContain":
                result = !value.contains(triggerValue);
                break;
            default:
                break;
        }

        // 记录结果
        if (sceneThingsModelItems == null) {
            sceneThingsModelItems = new ArrayList<>();
        }
//        SceneThingsModelItem sceneItem = new SceneThingsModelItem(scriptTemplate.getId(), matchItem.getValue(), type,
//                scriptTemplate.getScriptId(), scriptTemplate.getSceneId(), scriptTemplate.getProductId(), deviceNum,
//                matchItem.getRemark(), matchItem.getName(), scriptTemplate.getValue());
//        sceneThingsModelItems.add(sceneItem);
        return result;
    }

    private boolean isNumeric(String str) {
        Pattern pattern = compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
