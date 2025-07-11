package com.sydh.rule.cmp.fallback;

import com.yomahub.liteflow.annotation.FallbackCmp;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeIteratorComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@FallbackCmp
@LiteflowComponent("IteratorFallbackCmp")
public class IteratorFallbackCmp extends NodeIteratorComponent {
    @Override
    public Iterator<?> processIterator() throws Exception {
        System.out.println("IteratorFallbackCmp executed!");
        List<String> list = new ArrayList<String>(){{
            add("jack");add("mary");add("tom");
        }};
        return list.iterator();
    }
}
