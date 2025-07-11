package com.sydh.protocol.base.struc;

import com.sydh.protocol.base.model.WModel;
import com.sydh.protocol.util.ExplainUtils;
import com.sydh.protocol.util.IntTool;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 前置数量的集合报文结构
 * @author bill
 */
public class TotalCollectionStructure<T> extends BaseStructure<Collection<T>> {

    private final WModel<T> model;
    private final int totalUnit;
    private final IntTool intTool;

    public TotalCollectionStructure(WModel<T> model, int totalUnit){
        this.model =model;
        this.totalUnit =totalUnit;
        this.intTool = IntTool.getInstance(totalUnit);
    }


    @Override
    public Collection<T> readFrom(ByteBuf input) {
        int total = intTool.read(input);
        if (total <= 0)
            return null;
        ArrayList<T> list = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            T t = model.readFrom(input);
            list.add(t);
        }
        return list;
    }

    @Override
    public void writeTo(ByteBuf output, Collection<T> list) {
        if (list != null) {
            intTool.write(output, list.size());
            for (T t : list) {
                model.writeTo(output, t);
            }
        } else {
            intTool.write(output, 0);
        }
    }

    @Override
    public Collection<T> readFrom(ByteBuf input, ExplainUtils explain) {
        int total = intTool.read(input);
        explain.lengthField(input.readerIndex() - totalUnit, description + "数量", total, totalUnit);
        if (total <= 0)
            return null;
        ArrayList<T> list = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            T t = model.readFrom(input, explain);
            list.add(t);
        }
        return list;
    }

    @Override
    public void writeTo(ByteBuf output, Collection<T> list, ExplainUtils explain) {
        if (list != null) {
            int total = list.size();
            explain.lengthField(output.writerIndex(), description + "数量", total, totalUnit);
            intTool.write(output, total);
            for (T t : list) {
                model.writeTo(output, t, explain);
            }
        } else {
            explain.lengthField(output.writerIndex(), description + "数量", 0, totalUnit);
            intTool.write(output, 0);
        }
    }
}