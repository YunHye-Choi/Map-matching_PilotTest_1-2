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
        /*
        for (AdjacentNode adjacentNode : heads) {
            System.out.print( " [ " + adjacentNode.getNode().getNodeID() + " ] ");
            while (adjacentNode.getNextNode() != null) {
                System.out.print(adjacentNode);
                adjacentNode = adjacentNode.getNextNode();
            }
            System.out.println();
        }*/

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

<<<<<<<<< Temporary merge branch 1
        // origin route points와 랜덤하게 생성된 GPS points 출력하기
=========
        //유림이가 썼던 코드
        Point gpsPoint = new Point(0.0,0.0);
        /*candidateLink = gpsPoint.findRadiusLink(roadNetwork.linkArrayList,roadNetwork.nodeArrayList);
        ArrayList<Point> candidate = new ArrayList<>();
        for(int i=0;i<candidateLink.size();i++)//모든 candidate Link 순회 하며, involving node들만 모아서 'candidate'에 저장
        {
            candidate.addAll(findRadiusPoint(gpsPoint,candidateLink.get(i),2));
        }*/

        Point firstPoint = new Point(0.0,0.0);
        Candidate first = new Candidate(firstPoint,roadNetwork.getLink(0));
        matchingPointArrayList.add(first);
        // origin route points와 랜덤하게 생성된 GPS points 500ms에 한번씩 출력하기
        System.out.println("\n\nhere\n\n\n");
>>>>>>>>> Temporary merge branch 2
        for (int i = 0; i < gpsPointArrayList.size(); i++) {
            ArrayList<Link> candidateLink = new ArrayList<>();
            System.out.println(routePointArrayList.get(i));
            System.out.println(gpsPointArrayList.get(i));
            candidateLink.addAll(gpsPointArrayList.get(i).getPoint().findRadiusLink(roadNetwork.linkArrayList,roadNetwork.nodeArrayList));
            //System.out.println("candidateLink : "+candidateLink);
            ArrayList<Candidate> candidates= new ArrayList<>();
            for(int j=0;j<candidateLink.size();j++) {
                candidates.addAll(findRadiusCandidate(gpsPointArrayList.get(i).getPoint(), candidateLink.get(j), 3));
            }
            calculationTP(candidates,matchingPointArrayList.get(i),roadNetwork,heads);

            //System.out.println("candidate : "+candidates);
        }

        // 유림이가 썼던 코드 그대로 둘게..유네확인~
        Point gpsPoint = new Point(1.0,2.0);
        ArrayList<Link> candidateLink;
        candidateLink = gpsPoint.findRadiusLink(roadNetwork.linkArrayList,roadNetwork.nodeArrayList);
        ArrayList<Point> candidate = new ArrayList<>();
        for(int i=0;i<candidateLink.size();i++) //모든 candidate Link 순회 하며, involving node들만 모아서 'candidate'에 저장
            candidate.addAll(findRadiusPoint(gpsPoint,candidateLink.get(i),2));
        }*/

        for(int i=0; i<gpsPointArrayList.size(); i++){
            emission.Emission_Median(gpsPointArrayList.get(i), routePointArrayList.get(i));
            if(i>0){
                transition.Transition_Median(gpsPointArrayList.get(i-1), gpsPointArrayList.get(i),routePointArrayList.get(i-1), routePointArrayList.get(i));
            }//매칭된 point로 해야하나.. 실제 point로 해야하나.. 의문?
            //중앙값 저장
        }



        // origin route points와 랜덤하게 생성된 GPS points 500ms에 한번씩 출력하기
        System.out.println("\n\nhere\n\n\n");
        for (int i = 0; i < gpsPointArrayList.size(); i++) {
            ArrayList<Link> candidateLink = new ArrayList<>();
            System.out.println(routePointArrayList.get(i));
            System.out.println(gpsPointArrayList.get(i));
            candidateLink.addAll(gpsPointArrayList.get(i).getPoint().findRadiusLink(roadNetwork.linkArrayList, roadNetwork.nodeArrayList));
            System.out.println("candidateLink : " + candidateLink);
            ArrayList<Point> candidates = new ArrayList<>();
            for (int j = 0; j < candidateLink.size(); j++) {
                candidates.addAll(findRadiusPoint(gpsPointArrayList.get(i).getPoint(), candidateLink.get(j), 3));
            }
            System.out.println("candidate : " + candidates);
            //Thread.sleep(500); // 500ms 마다 출력

            /////////matching/////////////
            matching_success.add(Matching(candidates, gpsPointArrayList, routePointArrayList, matching_success, i+1)); //size 1부터 시작
            System.out.print("matching: ");
            System.out.println(matching_success.get(i)); //매칭된 point 출력
            System.out.println();

        }

        System.out.println("silver");
        for(int i =0; i< routePointArrayList.size(); i++){
            System.out.println(routePointArrayList.get(i));
        }


        System.out.println("here");
        for(int i =0; i< matching_success.size(); i++){
            System.out.println(matching_success.get(i));
        }

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

    public static ArrayList<Candidate> findRadiusCandidate(Point center, Link link, Integer Radius){
        ArrayList<Point> allInvolvingPoint = link.getInvolvingPointList();
        ArrayList <Candidate> resultCandidate= new ArrayList<>();
        for(int i=0;i<allInvolvingPoint.size();i++){
            if(coordDistanceofPoints(center,allInvolvingPoint.get(i))<=Radius) {
                Candidate candidate = new Candidate(allInvolvingPoint.get(i),link);
                resultCandidate.add(candidate);
            }
        }
        return resultCandidate;
    }
    public static void calculationTP(ArrayList<Candidate> cand,Candidate lastMatch,RoadNetwork roadNetwork,ArrayList<AdjacentNode> heads){
        Link mainLink = lastMatch.getInvolvedLink();
        ArrayList<Link> secondLink = AdjacentLink(mainLink,roadNetwork,heads);
        ArrayList<Link> thirdLink = new ArrayList<>();
        for(int i=0;i<secondLink.size();i++){
            thirdLink.addAll(AdjacentLink(secondLink.get(i),roadNetwork,heads));
        }
        for(int i=0;i<cand.size();i++){
            if(cand.get(i).getInvolvedLink()==mainLink) cand.get(i).setTp(3/5);
            else{
                for(int j=0;j<secondLink.size();j++){
                    if(secondLink.get(j)==cand.get(i).getInvolvedLink()) cand.get(i).setTp(2/5);
                }
                for(int j=0;j<thirdLink.size();j++){
                    if(thirdLink.get(j)==cand.get(i).getInvolvedLink()) cand.get(i).setTp(1/5);
                }
            }
        }
    }

    ////////////////////세정 추가 probability////////////////////

    public static Point Matching(ArrayList<Point> candidates, ArrayList<GPSPoint> gpsPointArrayList, ArrayList<Point> routePointArrayList, ArrayList<Point> matching_success, int size) {
        Point matching = new Point(0.0, 0.0);

        double maximum_tpep = 0;

        if(size==1 || size==2){
            double min_ep = 0;
            for(int i=0; i< candidates.size(); i++){
                if(i==0) {
                    min_ep = emission.Emission_pro(gpsPointArrayList.get(size - 1), candidates.get(i), size); //gpspoint
                    matching = candidates.get(i);
                }
                else if(min_ep > emission.Emission_pro(gpsPointArrayList.get(size-1), candidates.get(i), size) ) {
                    min_ep = emission.Emission_pro(gpsPointArrayList.get(size-1), candidates.get(i), size);
                    matching = candidates.get(i);
                }
            }
            return matching;
        }

    public static ArrayList<Link> AdjacentLink(Link mainLink,RoadNetwork roadNetwork,ArrayList<AdjacentNode> heads){
        int startNode=mainLink.getStartNodeID();
        int endNode = mainLink.getEndNodeID();
        ArrayList<Link> secondLink = new ArrayList<>();
        //ArrayList<Node> startAdjacentNode = new ArrayList<>();
        //ArrayList<Node> endAdjacentNode = new ArrayList<>();
        AdjacentNode pointer = heads.get(roadNetwork.nodeArrayList.get(startNode).getNodeID()).getNextNode();
        while(true){
            if(pointer==null) break;
            secondLink.add(roadNetwork.getLink(pointer.getNode().getNodeID(),startNode));
            pointer=pointer.getNextNode();
        }
        pointer = heads.get(roadNetwork.nodeArrayList.get(endNode).getNodeID()).getNextNode();
        while(true){
            if(pointer==null) break;
            secondLink.add(roadNetwork.getLink(pointer.getNode().getNodeID(),endNode));
            pointer=pointer.getNextNode();
        }
        return secondLink;
    }
}