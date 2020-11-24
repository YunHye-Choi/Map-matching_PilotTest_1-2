public class AdjacentNode {
    private Node node;
    private Link adjacentLink;
    private AdjacentNode nextNode;

    public AdjacentNode(Node node){
        this.node=node;
        adjacentLink=null;
        nextNode=null;
    }

    public AdjacentNode(Node node, Link adjacentLink){
        this.node=node;
        this.adjacentLink=adjacentLink;
        nextNode=null;
    }

    public AdjacentNode(Node node, Link adjacentLink,AdjacentNode nextNode){
        this.node=node;
        this.adjacentLink=adjacentLink;
        this.nextNode=nextNode;
    }

    public void setNextNode(AdjacentNode nextNode){
        this.nextNode=nextNode;
    }

    public AdjacentNode getNextNode(){return nextNode;}

    public Link getLink(){return adjacentLink;}

    public Node getNode() { return node; }

    // 윤혜가 추가한 출력서식 (그냥 확인용으로 쓰세용)

    @Override
    public String toString() {
        return "nextNode: "+ getNextNode().getNode().getNodeID() + ", "
                + "weight: "+ getNextNode().getLink().getWeight() + " | ";
    }

    public Link getAdjacentLink() {
        return adjacentLink;
    }

}