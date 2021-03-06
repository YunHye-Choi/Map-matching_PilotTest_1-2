import com.sun.deploy.util.SyncAccess;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("===== [YSY] Map-matching PilotTest 1-2 =====");
        int testNo = 2; // 여기만 바꿔주면 됨 (1-세정, 2-유네, 3-유림)
        FileIO fileIO = new FileIO(testNo);
        // 파일에서 읽어와 도로네트워크 생성
        RoadNetwork roadNetwork = fileIO.generateRoadNetwork();

        // Link와 Node를 바탕으로 Adjacent List 구축
        ArrayList<AdjacentNode> heads = new ArrayList<>();
        for(int i=0;i<roadNetwork.nodeArrayList.size();i++){
            AdjacentNode headNode = new AdjacentNode(roadNetwork.nodeArrayList.get(i));
            heads.add(headNode);

            List<Pair<Link,Integer>> adjacentLink = roadNetwork.getLink1(headNode.getNode().getNodeID());
            if(adjacentLink.size()==0) continue;
            AdjacentNode ptr = headNode;
            for(int j=0;j<adjacentLink.size();j++){
                AdjacentNode addNode = new AdjacentNode(roadNetwork.getNode(adjacentLink.get(j).getValue()),adjacentLink.get(j).getKey());
                ptr.setNextNode(addNode);
                ptr = ptr.getNextNode();
            }
        }
        // Adjacency List 구조 바탕으로 출력 test
        for (AdjacentNode adjacentNode : heads) {
            System.out.print( " [ " + adjacentNode.getNode().getNodeID() + " ] ");
            while (adjacentNode.getNextNode() != null) {
                System.out.print(adjacentNode);
                adjacentNode = adjacentNode.getNextNode();
            }
            System.out.println();
        }
        // GPS points와 routePoints를 저장할 ArrayList생성
        ArrayList<GPSPoint> gpsPointArrayList = new ArrayList<>();
        ArrayList<Point> routePointArrayList; // 실제 경로의 points!

        // test 번호에 맞는 routePoints생성
        routePointArrayList = roadNetwork.routePoints(testNo);

        // GPSPoints 생성 -> 이전 test1-1에서 generateGPSPoints메서드 삭제함
        int timestamp = 0;
        for (Point point : routePointArrayList) {
            GPSPoint gpsPoint = new GPSPoint(timestamp, point);
            gpsPointArrayList.add(gpsPoint);
            timestamp++;
        }

        // origin route points와 랜덤하게 생성된 GPS points 출력하기
        for (int i = 0; i < gpsPointArrayList.size(); i++) {
            System.out.println(routePointArrayList.get(i));
            System.out.println(gpsPointArrayList.get(i));
        }

        // 유림이가 썼던 코드 그대로 둘게..유네확인~
        Point gpsPoint = new Point(1.0,2.0);
        ArrayList<Link> candidateLink;
        candidateLink = gpsPoint.findRadiusLink(roadNetwork.linkArrayList,roadNetwork.nodeArrayList);
        ArrayList<Point> candidate = new ArrayList<>();
        for(int i=0;i<candidateLink.size();i++) //모든 candidate Link 순회 하며, involving node들만 모아서 'candidate'에 저장
            candidate.addAll(findRadiusPoint(gpsPoint,candidateLink.get(i),2));
    }

    public static Double coordDistanceofPoints(Point a, Point b){
        return Math.sqrt(Math.pow(a.getX()-b.getX(),2)+Math.pow(a.getY()-b.getY(),2));
    }//유클리드 거리 구하기

    // 이 아이를 어디 맵매칭클래스 같은거 만들어서 거기에 넣으면 좋지 않을까?하는 생각이어요~
    public static ArrayList<Point> findRadiusPoint(Point center, Link link, Integer Radius){//Link 안, 반경 내 involving node들만 반환
        ArrayList<Point> allInvolvingPoint =link.getInvolvingPointList();
        ArrayList<Point> resultPoint = new ArrayList<>();
        for(int i=0;i<allInvolvingPoint.size();i++){
            if(coordDistanceofPoints(center,allInvolvingPoint.get(i))<=Radius)
                resultPoint.add(allInvolvingPoint.get(i));
        }
        return resultPoint;
    }
}
