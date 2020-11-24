import javafx.util.Pair;
import sun.management.AgentConfigurationError;

import java.util.ArrayList;
import java.util.List;

public class RoadNetwork {

    // 데이터를 보관할 ArrayList들을 담는 class..
    // 필요 없을 수도 있지만 혹시의 상황을 대비해서 만들었음
    // 일단 이런 구조로 ArrayList로 선언해보고 이 클래스 정 필요 없겠다 싶으면 다음 테스트에서 다시 파기 예정
    // 만약 필요하게 되면 private으로 만들어주고 getter setter만들 예정
    protected ArrayList<Node> nodeArrayList = new ArrayList<>();
    protected ArrayList<Link> linkArrayList = new ArrayList<>();

    // _nodeID를 nodeID로 가지는 node반환
    public Node getNode (int _nodeID) {
        for (Node currNode : nodeArrayList) {
            if (currNode.getNodeID() == _nodeID) {
                return currNode;
            }
        }
        // 탐색에 실패한 경우 nodeId가 -1인 Node반환
        return new Node(-1, new Point((double)-99,(double)-99));
    }

    public Node getNode1 (Point nodePoint){
        for(Node currNode : nodeArrayList){
            if(currNode.getCoordinate()==nodePoint)
                return currNode;
        }
        // 탐색에 실패한 경우 nodeId가 -1인 Node반환
        return new Node(-1, new Point((double)-99,(double)-99));
    }

    // _linkID를 linkID로 가지는 link반환
    public Link getLink (int _linkID) {
        for (Link currLink : linkArrayList) {
            if (currLink.getLinkID() == _linkID) {
                return currLink;
            }
        }
        // 탐색에 실패한 경우 nodeId가 -1인 Link반환
        return new Link(-1,-1,-1,(double)-1);
    }
    // nodeID_s를 start node ID로, nodeID_e를 end node id로 가지는 가지는 link반환
    // 혹은 nodeID_e를 start node ID로, nodeID_s를 end node id로 가지는 가지는 link반환
    public Link getLink (int nodeID_s, int nodeID_e) {
        for (Link currLink : linkArrayList) {
            if ((currLink.getStartNodeID() == nodeID_s) && (currLink.getEndNodeID() == nodeID_e)
                    || (currLink.getStartNodeID() == nodeID_e) && (currLink.getEndNodeID() == nodeID_s)) {
                return currLink;
            }
        }
        // 탐색에 실패한 경우 nodeId가 -1인 Link반환
        return new Link(-1,-1,-1,(double)-1);
    }
    List<Pair<Link,Integer>> getLink1 (int nodeID) {
        List<Pair<Link,Integer>> pairs = new ArrayList<>();
        for (Link currLink : linkArrayList) {
            if (currLink.getStartNodeID() == nodeID) {
                pairs.add(new Pair<Link,Integer>(currLink,currLink.getEndNodeID()));
            }
            else if(currLink.getEndNodeID() == nodeID){
                pairs.add(new Pair<Link,Integer>(currLink,currLink.getStartNodeID()));
            }
        }
        return pairs;
    }

    // testNo에 맞게 경로 Point로 생성하는 작업
    // 아직  startNode가 닿는지 endNode가 닿는지에 따라 순서대로/역순으로 나오는 로직은 추가 안함
    /*왼쪽에서 오른쪽으로 가는 방향만 고려함 (왼, 오를 따질 수 없는 경우는 아래에서 위로 가는 방향만 고려)
     *되는 루트 →, ↑, ↗,↘
     *안되는 루트: ←, ↓, ↙, ↖
     * */
    public ArrayList<Point> routePoints (int testNo) {
        ArrayList<Point> routePoints = new ArrayList<>();
        if (testNo == 1) { // 세정이 데이터
            int[] routeNodes = {0, 1, 9, 10, 11, 19, 28, 36, 44, 45, 46, 47, 55};
            for (int i=0; i<routeNodes.length-1; i++) {
                routePoints.addAll(getLink(routeNodes[i], routeNodes[i+1]).getInvolvingPointList());
            }
        } else if (testNo == 2) { // 유네 데이터
            // node0 에서 node 55로 가는 경로
            int[] routeNodes = {0,1, 6, 12, 25, 26, 27, 33, 34};
            for (int i=0; i<routeNodes.length-1; i++) {
                routePoints.addAll(getLink(routeNodes[i], routeNodes[i+1]).getInvolvingPointList());
            }
        } else if (testNo == 3) { // 유림이 데이터
//gmdma
        } return routePoints;
    }

    // link개수 출력하기
    int getLinksSize () {
        return linkArrayList.size();
    }
}

