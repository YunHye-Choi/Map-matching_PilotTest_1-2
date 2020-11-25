import com.sun.deploy.util.SyncAccess;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    // added from branch test_merge
    private static Emission emission = new Emission();

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("===== [YSY] Map-matching PilotTest 1-2 =====");
        int testNo = 2; // 여기만 바꿔주면 됨 (1-세정, 2-유네, 3-유림)
        FileIO fileIO = new FileIO(testNo);

        // added from branch test_merge
        ArrayList<Point> matching_success = new ArrayList<>();

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

        // origin route points와 랜덤하게 생성된 GPS points 출력하기
        for (int i = 0; i < gpsPointArrayList.size(); i++) {
            System.out.println(routePointArrayList.get(i));
            System.out.println(gpsPointArrayList.get(i));
        }

        // Transition probability 구현
        int n = roadNetwork.getLinksSize();
        double [][] tp_matrix = new double[n][n];
        for (int i = 0; i < n;i++) {
            // 여기에서 link[i]가 몇개의 link와 맞닿아있는지 int 변수 선언해서 저장
            int m = roadNetwork.getLink(i).nextLinksNum(roadNetwork);
            // 알고리즘대로 tp 지정
            for (int j = 0; j < n; j++) {
                if (i == j) tp_matrix[i][j] = 1.0;
                else if (roadNetwork.getLink(i).isLinkNextTo(roadNetwork, j))
                    tp_matrix[i][j] = 1.0/m;
                else tp_matrix[i][j] = 0.0;
            }
        }

        // i - j - k로 맞닿아있을떄 tp[i][k] = tp[i][j] * tp[j][k]
        for (int i = 0; i < n;i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k< n; k++) {
                    if (i == k) continue;
                    if (roadNetwork.getLink(i).isLinkNextTo(roadNetwork, k)) continue;
                    if (roadNetwork.getLink(i).isLinkNextTo(roadNetwork, j) && roadNetwork.getLink(j).isLinkNextTo(roadNetwork, k))
                        tp_matrix[i][k] = tp_matrix[i][j] * tp_matrix[j][k];
                }
            }
        }

        /*// origin route points와 랜덤하게 생성된 GPS points 500ms에 한번씩 출력하기
        System.out.println("\n\nhere\n\n\n");
        for (int i = 0; i < gpsPointArrayList.size(); i++) {
            ArrayList<Link> candidateLink = new ArrayList<>();
            System.out.println(routePointArrayList.get(i));
            System.out.println(gpsPointArrayList.get(i));
            candidateLink.addAll(gpsPointArrayList.get(i).getPoint().findRadiusLink(roadNetwork.linkArrayList,roadNetwork.nodeArrayList));
            System.out.println("candidateLink : "+candidateLink);
            ArrayList<Candidate> candidates= new ArrayList<>();
            for(int j=0;j<candidateLink.size();j++) {
                ArrayList<Point> points = findRadiusPoint(gpsPointArrayList.get(i).getPoint(), candidateLink.get(j), 3);
                for (int k=0;k<points.size();k++) {
                    candidates.add(new Candidate(points.get(k), candidateLink.get(j)));
                }
            }
            System.out.println("candidate : "+candidates);
        }
        // 유림이가 썼던 코드 그대로 둘게..유네확인~

        Point gpsPoint = new Point(0.0,0.0);*/

        for(int i=0; i<gpsPointArrayList.size(); i++){
            emission.Emission_Median(gpsPointArrayList.get(i), routePointArrayList.get(i));

        }

        // origin route points와 랜덤하게 생성된 GPS points 500ms에 한번씩 출력하기
        System.out.println("\n\nhere\n\n\n");
        for (int i = 0; i < gpsPointArrayList.size(); i++) {
            ArrayList<Link> candidateLink = new ArrayList<>();
            //System.out.println(routePointArrayList.get(i));
            //System.out.println(gpsPointArrayList.get(i));
            candidateLink.addAll(gpsPointArrayList.get(i).getPoint().findRadiusLink(roadNetwork.linkArrayList, roadNetwork.nodeArrayList));
           // System.out.println("candidateLink : " + candidateLink);
            ArrayList<Candidate> candidates= new ArrayList<>();
            for(int j=0;j<candidateLink.size();j++) {
                ArrayList<Point> points = findRadiusPoint(gpsPointArrayList.get(i).getPoint(), candidateLink.get(j), 5);
                for (int k=0;k<points.size();k++) {
                    candidates.add(new Candidate(points.get(k), candidateLink.get(j)));
                }
            }
            //System.out.println("candidate : " + candidates);
            //Thread.sleep(500); // 500ms 마다 출력

            /////////matching/////////////
            matching_success.add(Matching(candidates, gpsPointArrayList,
                    routePointArrayList, matching_success, tp_matrix, roadNetwork.linkArrayList, i+1)); //size 1부터 시작
            System.out.print("matching: ");
            System.out.println(matching_success.get(i)); //매칭된 point 출력
            System.out.println();

        }
        double success_sum= 0;
        System.out.println("[Origin]\t->\t[Matched]");
        for(int i =0; i< matching_success.size(); i++){
            System.out.print(routePointArrayList.get(i) + "\t");
            System.out.println(matching_success.get(i));
            if (routePointArrayList.get(i) == matching_success.get(i)) success_sum ++;
        }
        System.out.println("Success prob = "+(100*(success_sum/(double)matching_success.size())) + "%");
        System.out.println(" Total: "+ matching_success.size() +"\n Succeed: "+success_sum+ "\n Failed: "+(matching_success.size()-success_sum));

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

    // added from branch test_merge
    ////////////////////세정 추가 probability////////////////////

    public static Point Matching(ArrayList<Candidate> candidates, ArrayList<GPSPoint> gpsPointArrayList,
                                 ArrayList<Point> routePointArrayList,ArrayList<Point> matching_success, double[][] tp, ArrayList<Link> links, int size) {
        Point matching = new Point(0.0, 0.0);

        double maximum_tpep = 0;

        if(size==1 || size==2){
            double min_ep = 0;
            for(int i=0; i< candidates.size(); i++){
                if(i==0) {
                    min_ep = emission.Emission_pro(gpsPointArrayList.get(size - 1), candidates.get(i).getPoint(), size); //gpspoint
                    matching = candidates.get(i).getPoint();
                }
                else if(min_ep > emission.Emission_pro(gpsPointArrayList.get(size-1), candidates.get(i).getPoint(), size) ) {
                    min_ep = emission.Emission_pro(gpsPointArrayList.get(size-1), candidates.get(i).getPoint(), size);
                    matching = candidates.get(i).getPoint();
                }
            }
            return matching;
        }

        maximum_tpep=0;
        for(int i =0; i< candidates.size(); i++){
            double tpep=0;
            double tp_ = tp[matching_success.get(size-2).involvedLinkID(links)][candidates.get(i).getInvolvedLink().getLinkID()];
            double ep_ = emission.Emission_pro(gpsPointArrayList.get(size-1), candidates.get(i).getPoint(), size);
            tpep = (ep_)* (1000000000*tp_);
            //System.out.println("Candidate: " +candidates.get(i));
            //System.out.println("tp: "+ tp_+" 비율tp: "+ 1000000000*tp_);
            //System.out.println("ep: "+ ep_+" 비율ep: "+ 0.00000001*ep_);
            //System.out.print("tpep : ");
            //System.out.println(tpep+"\n==============================");
            if(maximum_tpep < tpep){
                maximum_tpep = tpep;
                //System.out.println("*** maximum tpep update! : "+ maximum_tpep);
                matching = candidates.get(i).getPoint();
            }
        }

        return matching;
    }
}
