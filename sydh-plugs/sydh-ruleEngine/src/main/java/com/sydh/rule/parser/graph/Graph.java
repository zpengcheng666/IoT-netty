package com.sydh.rule.parser.graph;

import cn.hutool.core.collection.CollUtil;
import com.sydh.rule.parser.entity.ConditionGraphData;
import com.sydh.rule.parser.entity.FlowData;
import com.sydh.rule.parser.entity.line.Line;
import com.sydh.rule.parser.entity.node.Node;
import com.sydh.rule.parser.enums.RuleEnums;
import com.sydh.rule.parser.execption.LiteFlowELException;
import com.sydh.rule.util.RuleGsonUtil;
import com.yomahub.liteflow.builder.el.ELWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
public class Graph {
    // 邻接表
    Map<String, List<String>> adjList = new LinkedHashMap<>();
    // 反向邻接表
    Map<String, List<String>> reverseAdjList = new LinkedHashMap<>();

    private String flowName;

    List<Node> allNodes = new ArrayList<>();
    List<Line> allLines = new ArrayList<>();
    // 节点列表
    Map<String, Node> nodes = new LinkedHashMap<>();
    // source节点集合
    Set<String> sources = new LinkedHashSet<>();
    // target节点集合
    Set<String> targets = new LinkedHashSet<>();
    // 即在source中也在target中的节点
    Set<String> sourceTargets = new LinkedHashSet<>();
    // 即不在source中也不在target中的节点
    Set<String> singleNodes = new LinkedHashSet<>();

    // 特殊路径的节点
    List<Node> specialNodes = new ArrayList<>();
    Map<Node, List<Node>> specialNodeMap = new LinkedHashMap<>();

    // 并行节点
    List<Node> whenNodes;
    // 起始节点
    List<Node> startNodes;
    // 结束节点
    List<Node> endNodes;
    // 所有路径
    List<List<Node>> allPaths;
    // 并行分组不相交路径
    List<List<List<Node>>> groupPaths;
    // 所有路径（去除特殊路径节点）
    List<List<Node>> allFilterPaths;
    // 并行分组不相交路径（去除特殊路径节点）
    List<List<List<Node>>> groupFilterPaths;

    Map<Node,ConditionGraphData> conditionMap = new LinkedHashMap<>();;
    // 分段节点
    //List<Node> segmentationPoints;

    // 流程数据
    private FlowData data;

    public Graph() {
    }

    public Graph(String jsonData) {
        this.data = RuleGsonUtil.getGson().fromJson(jsonData, FlowData.class);
        init(this.data);
    }

    public Graph(FlowData data) {
        this.data = data;
        init(this.data);
    }

    public Graph(List<Node> nodes, List<Line> lines) {
        this.allNodes = nodes;
        this.allLines = lines;
        this.data = new FlowData(this.allNodes, this.allLines);
        init(new FlowData(this.allNodes, this.allLines));
    }

    public Graph(String flowName, List<Node> nodes, List<Line> lines) {
        this.allNodes = nodes;
        this.allLines = lines;
        this.flowName = flowName;
        init(new FlowData(this.allNodes, this.allLines));
    }

    // 是否是流程必要的节点
    private boolean isFlowNode(Node node) {
        switch (node.getType()) {
            case "note":
                return false;
            case "context":
                return false;
            default:
                return true;
        }
    }

    // 是否是流程必要的路径
    private boolean isFlowLine(Line line) {
        if (line != null && line.getData() != null) {
            switch (line.getData().getType()) {
                case "note":
                    return false;
                default:
                    return true;
            }
        }
        return true;
    }

    public void init(FlowData data) {
        //this.flowName = data.getName();
        this.allNodes = data.getNodes().stream().filter(this::isFlowNode).collect(Collectors.toList());
        this.allLines = data.getLines().stream().filter(this::isFlowLine).collect(Collectors.toList());
        this.whenNodes = this.allNodes.stream().filter(m -> m.getType().equals("when")).collect(Collectors.toList());
        this.allNodes.forEach(this::addNode);
        this.allLines.forEach(this::addLine);
        this.sources = this.allLines.stream().map(Line::getFrom).collect(Collectors.toSet());
        this.targets = this.allLines.stream().map(Line::getTo).collect(Collectors.toSet());
        this.sourceTargets = this.allLines.stream().map(Line::getFrom).filter(this.allLines.stream().map(Line::getTo).collect(Collectors.toSet())::contains).collect(Collectors.toSet());
        this.singleNodes = this.allNodes.stream().filter(this::isSingle).map(Node::getId).collect(Collectors.toSet());
        this.startNodes = getStartNodes();
        this.endNodes = getEndNodes();
        //所有路径
        this.allPaths = findAllPaths();
        //分组路径
        this.groupPaths = groupPathsByIntersection(this.allPaths);
        //所有路径(过滤特殊路径节点)
        this.allFilterPaths = findFlowPath(getStartNodes());
        //分组路径(过滤特殊路径节点)
        this.groupFilterPaths = groupPathsByIntersection(this.allFilterPaths);
        // 分段节点
//        List<Node> segmentationPoints = findSegmentationPoints(groupPaths.get(0));
//        List<List<Node>> processSegments = getProcessSegments(segmentationPoints);
        this.specialNodes = getSpecialNodes();
        this.conditionMap = getConditionMap();;
    }


    //添加节点
    public void addNode(Node node) {
        adjList.putIfAbsent(node.getId(), new ArrayList<>());
        reverseAdjList.putIfAbsent(node.getId(), new ArrayList<>());
        nodes.put(node.getId(), node);
    }

    //添加路径
    public void addLine(Line line) {
        adjList.get(line.getFrom()).add(line.getTo());
        reverseAdjList.get(line.getTo()).add(line.getTo());
    }

    //判断一个节点是否是单节点，即节点在data.getLines的source和target中都没有出现
    public boolean isSingle(Node node) {
        return GraphUtil.isSingle(node, this.allLines);
    }

    //获取子节点集合
    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        List<String> neighborIds = adjList.get(node.getId()); // 获取邻接ID列表
        if (neighborIds != null) {
            for (String id : neighborIds) {
                neighbors.add(nodes.get(id)); // 通过ID获取节点
            }
        }
        return neighbors;
    }

    public boolean isNeighbor(Node node1, Node node2) {
        List<String> neighbors = adjList.get(node1.getId()); // 获取第一个节点的邻接ID列表
        return neighbors != null && neighbors.contains(node2.getId()); // 检查第二个节点的ID是否在列表中
    }

    public List<Node> getPrev(Node node) {
        List<Node> neighbors = new ArrayList<>();
        List<String> neighborIds = reverseAdjList.get(node.getId()); // 获取反向邻接ID列表
        if (neighborIds != null) {
            for (String id : neighborIds) {
                neighbors.add(nodes.get(id)); // 通过ID获取节点
            }
        }
        return neighbors;
    }

    // 判断是否有下一个节点
    public boolean hasNextNode(Node node) {
        return CollUtil.isNotEmpty(adjList.get(node.getId()));
    }

    //获取开始节点集合，根据sourcs和targets
    public List<Node> getStartNodes() {
        return GraphUtil.getStartNodes(targets, this.allNodes);
    }

    //获取开始结束集合，根据sourcs和targets
    public List<Node> getEndNodes() {
        return GraphUtil.getEndNodes(sources, this.allNodes);
    }

    public List<Node> getSpecialNodes() {
        List<Line> lines = this.allLines.stream().filter(Line::isSpecialPath).collect(Collectors.toList());
        Set<String> targetNodeIdSet = lines.stream().map(Line::getTo).collect(Collectors.toSet());
        List<Node> nodes = this.allNodes.stream().filter(m -> targetNodeIdSet.contains(m.getId())).collect(Collectors.toList());
        Set<Node> nodeSet = new HashSet<>();
        for (Node node : nodes) {
            List<List<Node>> paths = findAllPaths(node);
            List<Node> nodeList = paths.stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            nodeSet.addAll(nodeList);
        }
        return new ArrayList<>(nodeSet);
    }

    public Map<Node,ConditionGraphData> getConditionMap() {
        List<Node> conditionList = this.getNodes(RuleEnums.NodeEnum.CONDITION.getType());
        for (Node c : conditionList) {
            List<Node> neighborsList = this.getNeighbors(c);
            for(Node t : neighborsList) {
                ConditionGraphData conditionGraphData = this.conditionMap.get(t);
                if(conditionGraphData == null) {
                    conditionGraphData = new ConditionGraphData();
                    conditionGraphData.setTarget(t);
                    List<Node> clist = new ArrayList<>();
                    clist.add(c);
                    conditionGraphData.setCondition(clist);
                    this.conditionMap.put(t, conditionGraphData);
                } else {
                    conditionGraphData.getCondition().add(c);
                }
            }
        }
        return this.conditionMap;
    }

    // 判断allPaths是否存在相交点，并将有交点的路径分为一组，返回List<List<List<Node>>>
    // 判断路径是否存在交点，并按交点分组
    public List<List<List<Node>>> groupPathsByIntersection(List<List<Node>> allPaths) {
        return GraphUtil.groupPathsByIntersection(allPaths);
    }

    private List<Node> findAllPathsNodes(List<Node> startNodes) {
        List<List<Node>> paths = findAllPaths(startNodes);
        return paths.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    // 过滤特殊路径节点
    public List<List<Node>> findAllPaths(boolean filter) {
        List<List<Node>> newPaths = new ArrayList<>();
        List<List<Node>> paths = findAllPaths();
        if (filter) {
            for (List<Node> nodeList : paths) {
                List<Node> newNodes = new ArrayList<>();
                for (int i = 1; i < nodeList.size(); i++) {
                    boolean specialPath = isSpecialPath(nodeList.get(i));
                    newNodes.add(nodeList.get(i - 1));
                    if (specialPath) {
                        break;
                    }
                }
                newPaths.add(newNodes);
            }
        }
        return newPaths;
    }

    //找到所有路径的方法
    public List<List<Node>> findAllPaths() {
        List<List<Node>> allPaths = new ArrayList<>();
        List<Node> startNodes = getStartNodes();
        for (Node startNode : startNodes) {
            List<Node> visited = new ArrayList<>();
            GraphUtil.findAllPathsHelper(startNode, visited, allPaths, adjList, nodes);
        }
        return allPaths;
    }

    public List<List<Node>> findAllPaths(List<Node> startNodes) {
        List<List<Node>> allPaths = new ArrayList<>();
        for (Node startNode : startNodes) {
            List<Node> visited = new ArrayList<>();
            GraphUtil.findAllPathsHelper(startNode, visited, allPaths, adjList, nodes);
        }
        return allPaths;
    }

    public List<List<Node>> findAllPaths(Node startNode) {
        List<List<Node>> allPaths = new ArrayList<>();
        List<Node> visited = new ArrayList<>();
        GraphUtil.findAllPathsHelper(startNode, visited, allPaths, adjList, nodes);
        return allPaths;
    }

    public List<List<Node>> findAllPaths(Node startNode, boolean excludeStartNode) {
        List<List<Node>> allPaths = new ArrayList<>();
        List<Node> visited = new ArrayList<>();
        GraphUtil.findAllPathsHelper(startNode, visited, allPaths, adjList, nodes);
        if (excludeStartNode) {
            allPaths = allPaths.stream().map(m -> m.stream().filter(n -> n != startNode).collect(Collectors.toList())).collect(Collectors.toList());
        }
        return allPaths;
    }

    // 找到所有路径的方法,根据起始节点和结束节点
    public List<List<Node>> findAllPaths(Node startNode, Node endNode, boolean excludeStartNode, boolean excludeEndNode) {
        List<List<Node>> allPaths = findAllPaths(startNode.getId(), endNode.getId());
        if (excludeStartNode) {
            allPaths = allPaths.stream().map(m -> m.stream().filter(n -> n != startNode).collect(Collectors.toList())).collect(Collectors.toList());
        }
        if (excludeEndNode) {
            allPaths = allPaths.stream().map(m -> m.stream().filter(n -> n != endNode).collect(Collectors.toList())).collect(Collectors.toList());
        }
        return allPaths;
    }

    public List<List<Node>> findAllPaths(String startId, String endId) {
        List<List<Node>> paths = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        GraphUtil.findPathsRecursive(startId, endId, new ArrayList<>(), paths, visited, adjList, nodes);
        return paths;
    }

    //获取并行分段集合
    /*public List<List<List<Node>>> getProcessSegments() {
        if(startNodes.size() == 1 && endNodes.size() == 1){
            return new ArrayList<List<List<Node>>>(){{add(getProcessSegments(startNodes.get(0).getId(), endNodes.get(0).getId()));}};
        }else if(startNodes.size() == 1 && endNodes.size() == 0){
            return new ArrayList<List<List<Node>>>(){{add(getProcessSegments(startNodes.get(0).getId(), null));}};
        }else if(startNodes.size() > 1){
            Node commonNode = findCommonNode(startNodes);
            List<List<List<Node>>> lists = new ArrayList<>();
            if(commonNode == null){
                for (Node node : startNodes){
                    lists.add(getProcessSegments(node.getId(), null));
                }
            }else{
                lists.add(getProcessSegments(commonNode.getId(), null));
                throw new RuntimeException("流程存在多起点并且节点相交！");
            }
            return lists;
        }
        throw new RuntimeException("流程存在多起点、多结束点！");
    }*/


    //获取分段集合
    public List<List<Node>> getProcessSegments(String startId, String endId) {
        List<Node> segmentationPoints = GraphUtil.findSegmentationPoints(findAllPaths(startId, endId), nodes);
        Set<String> segmentPointsSet = new HashSet<>();
        for (Node node : segmentationPoints) {
            segmentPointsSet.add(node.id);
        }
        segmentPointsSet.add(endId); // 确保终点也被视为分段点

        Map<String, Node> visited = new HashMap<>();
        List<List<Node>> segments = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(nodes.get(startId));

        List<Node> currentSegment = new ArrayList<>();
        boolean isSegmentStart = true;

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            if (visited.containsKey(currentNode.id)) {
                continue;
            }
            visited.put(currentNode.id, currentNode);

            if (segmentPointsSet.contains(currentNode.id)) {
                if (!currentSegment.isEmpty()) {
                    segments.add(new ArrayList<>(currentSegment));
                    currentSegment.clear();
                }
                // 分段点单独成段
                segments.add(Collections.singletonList(currentNode));
                isSegmentStart = true;
            } else {
                if (isSegmentStart) {
                    isSegmentStart = false;
                    currentSegment = new ArrayList<>();
                }
                currentSegment.add(currentNode);
            }

            List<String> neighbors = adjList.get(currentNode.id);
            if (neighbors != null) {
                for (String neighbor : neighbors) {
                    if (!visited.containsKey(neighbor)) {
                        queue.offer(nodes.get(neighbor));
                    }
                }
            }
        }

        // 添加最后一个非空段
        if (!currentSegment.isEmpty()) {
            segments.add(currentSegment);
        }

        return segments;
    }

    public ELWrapper toELWrapper() throws LiteFlowELException {
        return GraphToEL.toEL(this);
    }

    public String toEL(boolean format) throws LiteFlowELException {
        if (this.getAllNodes().isEmpty()) {
            return "";
        }
        return GraphToEL.toEL(this).toEL(format);
    }

    /**
     * 通过起始节点集合获取相交节点
     *
     * @param startNodes
     * @return
     */
    public Node findJoinNode(List<Node> startNodes) {
        if (startNodes == null || startNodes.isEmpty()) {
            return null;
        }

        // 创建一个用于存储每个节点到达的所有节点的map
        Map<String, Set<String>> allPaths = new HashMap<>();

        // 对每个起始节点进行深度优先搜索，记录路径
        for (Node startNode : startNodes) {
            Set<String> visited = new HashSet<>();
            GraphUtil.dfs(startNode.getId(), visited, adjList);
            allPaths.put(startNode.getId(), visited);
        }

        // 找到所有路径中共有的节点
        Set<String> commonNodes = new HashSet<>(allPaths.values().iterator().next());
        for (Set<String> path : allPaths.values()) {
            commonNodes.retainAll(path);
        }

        // 返回第一个公共节点
        for (String nodeId : commonNodes) {
            return nodes.get(nodeId);
        }

        return null;
    }

    public Node findJoinNode(Node startNode) {
        List<List<Node>> allPaths = findAllPaths(startNode);
        // 多条路径获取聚合节点
        if (allPaths.size() > 1) {
            List<Node> segmentationPoints = GraphUtil.findSegmentationPoints(allPaths, this.getNodes());
            segmentationPoints.remove(startNode);
            return CollUtil.isNotEmpty(segmentationPoints) ? segmentationPoints.get(0) : null;
        }
        return null;
    }

    public List<Node> getNodes(String type) {
        return data.getNodes().stream().filter(m -> m.getType().equals(type)).collect(Collectors.toList());
    }

    public Line getLine(Node sourceNode, Node targetNode) {
        return getAllLines().stream().filter(m -> m.getFrom().equals(sourceNode.getId()) && m.getTo().equals(targetNode.getId())).findFirst().orElse(null);
    }

    //获取指定节点下的特殊路径
    public List<Line> getLines(Node node, RuleEnums.PATH_ENUM path) {
        List<Line> lines = new ArrayList<>();
        List<Node> neighbors = getNeighbors(node);
        for (Node next : neighbors) {
            Line line = getLine(node, next);
            if (line.isPath(path)) {
                lines.add(line);
            }
        }
        return lines;
    }

    // 获取包含指定节点的路径
    public List<Line> getLines(List<Node> nodes) {
        Set<String> idSet = nodes.stream().map(Node::getId).collect(Collectors.toSet());
        return this.allLines.stream().filter(m -> idSet.contains(m.getFrom()) && idSet.contains(m.getTo())).collect(Collectors.toList());
    }

    //是否特殊路径
    public boolean isSpecialPath(Node targetNode) {
        List<Node> prevList = getPrev(targetNode);//前一个节点
        if (prevList.size() == 1) {
            Line line = getLine(prevList.get(0), targetNode);
            if (line != null) {
                return line.isSpecialPath();
            }
        }
        return false;
    }

    //是否特殊路径
    public boolean isSpecialPath(Node sourceNode, Node targetNode, RuleEnums.PATH_ENUM pathEnum) {
        Line line = getLine(sourceNode, targetNode);
        if (line != null) {
            return line.isSpecialPath();
        }
        return false;
    }

    public boolean isPath(Node sourceNode, Node targetNode, RuleEnums.PATH_ENUM pathEnum) {
        Line line = getLine(sourceNode, targetNode);
        if (line != null) {
            return line.isPath(pathEnum);
        }
        return false;
    }

    //从node节点开始遍历获取 特殊路径 节点集合
    public List<Node> getSpecialPathNodes(Node node, RuleEnums.PATH_ENUM pathEnum) {
        List<List<Node>> allPathNodes = findAllPaths(node);
        Set<Node> nodeSet = new LinkedHashSet<>();
        for (List<Node> pathNodes : allPathNodes) {
            List<Node> nodeList = pathNodes.stream().filter(m -> !"end".equalsIgnoreCase(m.getType())).collect(Collectors.toList());
            if (nodeList.size() > 2 && isPath(nodeList.get(1), nodeList.get(2), pathEnum)) {
                nodeSet.addAll(nodeList);
            }
        }
        nodeSet.remove(node);
        return new ArrayList<>(nodeSet);
    }

    public Node getNode(String nodeId) {
        return getAllNodes().stream().filter(m -> m.getId().equals(nodeId)).findFirst().orElse(null);
    }


    public Graph getGraph() {
        Set<Node> nodeSet = new HashSet<>();
        List<String> typeList = CollUtil.toList("subflow", "subvar", "router");
        for (List<Node> list : allPaths) {
            if (!typeList.contains(list.get(0).getType())) {
                nodeSet.addAll(list);
            }
        }
        List<Node> nodeList = new ArrayList<>(nodeSet);
        return new Graph(nodeList, getLines(nodeList));
    }

    public List<Graph> getSubFlowGraph() {
        Map<String, Set<Node>> nodeMap = new LinkedHashMap<>();
        for (List<Node> list : allPaths) {
            if ("subflow".equals(list.get(0).getType())) {
                String chainName = list.get(0).getNodeData().getNodeDataSubflow().getChainName();
                if (nodeMap.containsKey(chainName)) {
                    nodeMap.get(chainName).addAll(new HashSet<>(list));
                } else {
                    nodeMap.put(chainName, new HashSet<>(list));
                }
            }
        }
        return nodeMap.entrySet().stream().map(m -> new Graph(m.getKey(), new ArrayList<>(m.getValue()), getLines(new ArrayList<>(m.getValue())))).collect(Collectors.toList());
    }

    public List<Graph> getSubVarGraph() {
        Map<String, Set<Node>> nodeMap = new LinkedHashMap<>();
        for (List<Node> list : allPaths) {
            if ("subvar".equals(list.get(0).getType())) {
                String varName = list.get(0).getNodeData().getNodeDataSubvar().getVarName();
                if (nodeMap.containsKey(varName)) {
                    nodeMap.get(varName).addAll(new HashSet<>(list));
                } else {
                    nodeMap.put(varName, new HashSet<>(list));
                }
            }
        }
        return nodeMap.entrySet().stream().map(m -> new Graph(m.getKey(), new ArrayList<>(m.getValue()), getLines(new ArrayList<>(m.getValue())))).collect(Collectors.toList());
    }

    public Graph getRouterGraph() {
        Map<String, Set<Node>> nodeMap = new LinkedHashMap<>();
        for (List<Node> list : allPaths) {
            if ("router".equals(list.get(0).getType())) {
                String namespace = list.get(0).getNodeData().getNodeDataRouter().getNamespace();
                if (nodeMap.containsKey(namespace)) {
                    nodeMap.get(namespace).addAll(new HashSet<>(list));
                } else {
                    nodeMap.put(namespace, new HashSet<>(list));
                }
            }
        }
        return nodeMap.entrySet().stream().map(m -> new Graph(m.getKey(), new ArrayList<>(m.getValue()), getLines(new ArrayList<>(m.getValue())))).collect(Collectors.toList()).stream().findFirst().orElse(null);
    }

    public GraphInfo toELInfo() throws LiteFlowELException {
        GraphInfo graphInfo = new GraphInfo();
        //主流程
        Graph mainGraph = this.getGraph();
        graphInfo.setMainGraph(mainGraph);
        ELWrapper elWrapper = mainGraph.toELWrapper();
        graphInfo.setMainGraphELWrapper(elWrapper);
        if (!mainGraph.getAllNodes().isEmpty()) {
            graphInfo.setMainELStr(elWrapper.toEL(data.getFormat()));
        }

        //上下文
        List<Node> contextNode = this.getNodes("context");
        graphInfo.setContextList(contextNode.stream().map(m -> m.getNodeData().getNodeDataContext()).collect(Collectors.toList()));

        //子流程
//        List<Graph> subFlowGraphs = this.getSubFlowGraph();
//        Map<String,ELWrapper> subFlowELWrapperMap = new HashMap<>();
//        Map<String,String> subFlowELMap = new HashMap<>();
//        for (Graph subFlowGraph : subFlowGraphs){
//            subFlowELWrapperMap.put(subFlowGraph.getFlowName(), subFlowGraph.toELWrapper());
//            subFlowELMap.put(subFlowGraph.getFlowName(), (data.getFormat() ? System.lineSeparator() : "")+ subFlowGraph.toEL(data.getFormat()));
//        }
//        graphInfo.setSubFlowGraphs(subFlowGraphs);
//        graphInfo.setSubFlowELWrapperMap(subFlowELWrapperMap);
//        graphInfo.setSubFlowELMap(subFlowELMap);
//
        return graphInfo;
    }

    //找到所有路径的方法
    public List<List<Node>> findFlowPath(List<Node> startNodes) {
        List<List<Node>> allPaths = new ArrayList<>();
        for (Node startNode : startNodes) {
            List<Node> visited = new ArrayList<>();
            findFlowPathHelper(startNode, visited, allPaths, adjList, nodes);
        }
        return allPaths;
    }

    //找到所有路径的方法
    public void findFlowPathHelper(Node node, List<Node> path, List<List<Node>> allPaths, Map<String, List<String>> adjList, Map<String, Node> nodes) {
        path.add(node);
        if (!adjList.containsKey(node.getId()) || adjList.get(node.getId()).isEmpty()) {
            allPaths.add(new ArrayList<>(path));
        } else {
            boolean isAllSpecialPath = true;
            for (String neighborId : adjList.get(node.getId())) {
                Node neighbor = nodes.get(neighborId);
                //非特殊路径
                if (!isSpecialPath(neighbor)) {
                    isAllSpecialPath = false;
                    if (!path.contains(neighbor)) {
                        findFlowPathHelper(neighbor, new ArrayList<>(path), allPaths, adjList, nodes);
                    }
                }
            }
            if (isAllSpecialPath) {
                allPaths.add(new ArrayList<>(path));
            }
        }
        path.remove(node);
    }
}
