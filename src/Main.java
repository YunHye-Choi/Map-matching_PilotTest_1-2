import com.sun.deploy.util.SyncAccess;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    // added from branch test_merge
    private static Emission emission = new Emission();

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("===== [YSY] Map-matching PilotTest 1-2 =====");
        int testNo = 2; // 여기만 바꿔주면 됨 (1-세정, 2-유네, 3-유림)
        // 파일럿 테스트 1-1 :
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
        System.out.println("zzz\n"+gpsPointArrayList.size());
        // origin route points와 랜덤하게 생성된 GPS points 출력하기
        for (int i = 0; i < gpsPointArrayList.size(); i++) {
            System.out.println(routePointArrayList.get(i));
            System.out.println(gpsPointArrayList.get(i));
        }

        ///////////// Transition probability 구하기 ////////////////
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
        /////////////////////////////////////////////////////////////////

        for(int i=0; i<gpsPointArrayList.size(); i++){
            emission.Emission_Median(gpsPointArrayList.get(i), routePointArrayList.get(i));
        }

        // origin route points와 랜덤하게 생성된 GPS points 500ms에 한번씩 출력하기
        System.out.println("\n\nhere\n\n");


        ArrayList<ArrayList<Candidate>> arrOfCandidates = new ArrayList<>(); // window size만큼의 t-window, ... , t-1, t에서의 candidates의 arrayList

        /////// candidates 구하기 (arrOfCandidates 초기화)///////////
        for (int i = 0; i < gpsPointArrayList.size(); i++) {
            ArrayList<Link> candidateLink = new ArrayList<>();
            candidateLink.addAll(gpsPointArrayList.get(i).getPoint().findRadiusLink(roadNetwork.linkArrayList, roadNetwork.nodeArrayList));
            ArrayList<Candidate> candidates = new ArrayList<>();
            for (int j = 0; j < candidateLink.size(); j++) {
                ArrayList<Point> points = findRadiusPoint(gpsPointArrayList.get(i).getPoint(), candidateLink.get(j), 3);
                for (int k = 0; k < points.size(); k++) {
                    Candidate candidate= new Candidate(points.get(k), candidateLink.get(j));
                    candidates.add(candidate);
                    candidate.setEmissionProb(emission.Emission_pro(gpsPointArrayList.get(i), candidate.getPoint(), k+1));
                }
            }
            arrOfCandidates.add(candidates);
        }

        ///////////// matching /////////////
        // window size 입력받기
        System.out.print("Fixed Sliding Window Viterbi. Window size: ");
        Scanner scanner = new Scanner(System.in);
        int wSize = scanner.nextInt(); // window size

        /*// window size보다 작은 경우의 gps point들은 emission prob만 따져서 매칭됨
        for (int i = 0; i < wSize-1; i++) {
            Point matching = new Point(0.0, 0.0);
            ArrayList<Candidate> candidates = arrOfCandidates.get(i);
            double min_ep = 0;
            for(int j=0; j< candidates.size(); j++){
                if(j == 0) {
                    arrOfCandidates.get(i).get(j).setEmissionProb(emission.Emission_pro(gpsPointArrayList.get(i), candidates.get(i).getPoint(), i+1)); //gpspoint
                    matching = candidates.get(i).getPoint();
                }
                else if(min_ep > emission.Emission_pro(gpsPointArrayList.get(i), candidates.get(i).getPoint(), i+1) ) {
                    min_ep = emission.Emission_pro(gpsPointArrayList.get(i), candidates.get(i).getPoint(), i+1);
                    matching = candidates.get(i).getPoint();
                }
            }
            matching_success.add(matching);
        }*/
        ArrayList<Point[]> subpaths = new ArrayList<>();
        System.out.println("\n\nhello\n\n");
        // arrOfCandidates를 순회하며 subpath의 마지막 point를 matching_success에 추가하는 loop
        for (int t = wSize-1; t < arrOfCandidates.size(); t++) {
            Point matching;
            double maximum_tpep = 0;
            Point subpath[] = new Point [wSize-1];
            //Link subpath[] = new Link [wSize-1]; // link 매칭을 시도한 흔적..
            int indexOfsubpath[] = new int [wSize];

            // 현재 candidates와 다음 candidates로 가는 t.p와 e.p곱 중 최대 값을 가지는 curr와 그 index를 maximum_tpep[현재]에 저장
            int index = 0;
            for (int i = t - wSize + 1; i < t; i++) {
                ArrayList<Candidate> curr_candidates = arrOfCandidates.get(i);
                ArrayList<Candidate> next_candidates = arrOfCandidates.get(i+1);
                maximum_tpep = 0;
                System.out.println("========"+gpsPointArrayList.get(i)+"========");
                for (Candidate cc : curr_candidates) {
                    System.out.println("Curr: "+cc.getPoint());
                    for (Candidate nc : next_candidates) {

                        //System.out.println("ep: "+cc.getEmissionProb() +" tp: "+tp_matrix[cc.getInvolvedLink().getLinkID()][nc.getInvolvedLink().getLinkID()]);
                        double prob = cc.getEmissionProb() * tp_matrix[cc.getInvolvedLink().getLinkID()][nc.getInvolvedLink().getLinkID()];
                        if(maximum_tpep < prob) {
                            maximum_tpep = prob;
                            indexOfsubpath[index] = curr_candidates.indexOf(cc);
                            System.out.print("["+cc.getPoint() + "] -> [" + nc.getPoint() + "]\t");
                            System.out.println("maximum tpep updated: "+maximum_tpep);
                        }
                    }
                    //System.out.println("curr: [" + cc.getPoint() + "], maximum tpep: "+maximum_tpep);
                }
                index ++;
            }

            // indexOfSubpath에 subpath의 index가 순서대로 저장됨
            // 이를 backtracking 하여 subpath 생성
            int j = t-1;
            for (int i = wSize -2; i>=0;i--) {
                subpath[i] = arrOfCandidates.get(j).get(indexOfsubpath[i]).getPoint();
                j--;
            }

            subpaths.add(subpath);
            //subpath의 끝 점 매칭
            matching = subpath[subpath.length-1];
            matching_success.add(matching);
            System.out.println("t: " + t);
        }
        // subpath출력..덜덜
        int t = 0;
        for (Point[] subpath : subpaths) {
            System.out.print(t + "] ");
            for (int  i=0;i<subpath.length;i++) {
                System.out.print("["+subpath[i] + "]");
                if (i!=subpath.length-1)
                    System.out.print(" ㅡ ");
            }
            System.out.println(); t++;
        }
        
        // origin->matched 출력
        double success_sum= 0;
        System.out.println("[Origin]\t->\t[Matched]");
        for(int i = 0; i< matching_success.size() ; i++){
            System.out.print("[" + routePointArrayList.get(i+1) + "] -> [");
            System.out.println(matching_success.get(i)+ "]");
            if (i != 0 && routePointArrayList.get(i+1) == matching_success.get(i)) success_sum ++;

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

    /*//////////////////// FSW viterbi 메서드////////////////////
    public static Point FSW(ArrayList<Candidate> candidates, ArrayList<GPSPoint> gpsPointArrayList
            , double[][] tp, ArrayList<Link> links, int t, int wSize) {
        Point matching = new Point(0.0, 0.0);

        double maximum_tpep = 0;

        if(t < wSize){
            double min_ep = 0;
            for(int i=0; i< candidates.size(); i++){
                if(i==0) {
                    min_ep = emission.Emission_pro(gpsPointArrayList.get(t - 1), candidates.get(i).getPoint(), t); //gpspoint
                    matching = candidates.get(i).getPoint();
                }
                else if(min_ep > emission.Emission_pro(gpsPointArrayList.get(t-1), candidates.get(i).getPoint(), t) ) {
                    min_ep = emission.Emission_pro(gpsPointArrayList.get(t-1), candidates.get(i).getPoint(), t);
                    matching = candidates.get(i).getPoint();
                }
            }
            return matching;
        }

        maximum_tpep=0;
        for(int i =0; i< candidates.size(); i++){
            double tpep=0;
            double tp_ = tp[matching_success.get(t-2).involvedLinkID(links)][candidates.get(i).getInvolvedLink().getLinkID()];
            double ep_ = emission.Emission_pro(gpsPointArrayList.get(t-1), candidates.get(i).getPoint(), t);
            tpep = (ep_)* (1000000000*tp_);
            *//* // for test
            System.out.println("Candidate: " +candidates.get(i));
            System.out.println("tp: "+ tp_+" 비율tp: "+ 1000000000*tp_);
            System.out.println("ep: "+ ep_+" 비율ep: "+ 0.00000001*ep_);
            System.out.print("tpep : ");
            System.out.println(tpep+"\n==============================");
            *//*
            if(maximum_tpep < tpep){
                maximum_tpep = tpep;
                matching = candidates.get(i).getPoint();
            }
        }

        return matching;
    }*/
}
