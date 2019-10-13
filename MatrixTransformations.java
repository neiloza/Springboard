package example;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.builtinprocs.BuiltInProcedures;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;


import java.util.*;
import java.util.stream.Stream;

public class MatrixTransformations {
    @Context
    public Log log;
    @Context
    public GraphDatabaseService db;
    @Procedure(value="example.adjacency_matrix")
    @Description("Takes in pairs of related nodes and converts it to an adjacency matrix")
    public Stream<Node> adjacency_martrix(@Name("start_node") List<Node> n1, @Name("end_node") List<Node> n2){
        Set<Node> all_nodes= new HashSet<Node>(n1);
        all_nodes.addAll(n2);
        int dimensions=all_nodes.size();
        INDArray adjMatrix= Nd4j.zeros(dimensions,dimensions);
        int counter=0;
        Map<Node,Integer> amim= new HashMap<Node, Integer>();
        for(Node node: all_nodes){
            amim.put(node, counter);
            counter=counter+1;
        }
        int number_of_relationships= n1.size();
        for(int i=0;i<number_of_relationships;i++){
            Node startNode= n1.get(i);
            Node endNode=n2.get(i);
            adjMatrix.put(amim.get(startNode),amim.get(endNode),1);
            adjMatrix.put(amim.get(endNode),amim.get(startNode),1);
        }

        // adjMatrix contains the desired adjacency matrix. amim contains a map relating nodes to indices in the matrix.
        // Not quite sure how to output INDARRAY to neo4j, since it lacks Stream implementation

        return null;

    }
    /**
    @Procedure(value="example.sparse_adjacency_matrix")
    @Description("Creates an adjacency matrix using tensorflow sparse tensor")
    public Stream<Integer> sparse_adjacency_matrix(){
        SparseTensor final_tensor=null;
        return null;
    }*/
    @Procedure(value="example.normalized_adjacency_matrix")
    @Description("Creates a normalized adjacency matrix")
    public INDArray normalized_adj_matrix(@Name("start_node") List<Node> n1, @Name("end_node") List<Node> n2){
        Set<Node> all_nodes= new HashSet<Node>(n1);
        all_nodes.addAll(n2);
        int dimensions=all_nodes.size();
        INDArray adjMatrix= Nd4j.zeros(dimensions,dimensions);
        int counter=0;
        Map<Node,Integer> amim= new HashMap<Node, Integer>();
        for(Node node: all_nodes){
            amim.put(node, counter);
            counter=counter+1;
        }
        int number_of_relationships= n1.size();
        for(int i=0;i<number_of_relationships;i++){
            Node startNode= n1.get(i);
            Node endNode=n2.get(i);
            adjMatrix.put(amim.get(startNode),amim.get(endNode),1);
            adjMatrix.put(amim.get(endNode),amim.get(startNode),1);
        }
        INDArray degree_matrix= Nd4j.zeros(dimensions,dimensions);
        INDArray degree_list=adjMatrix.sum(0);
        for (int i=0; i<dimensions;i++){
            degree_matrix.put(i,i,degree_list.getDouble(0,i));
        }
        INDArray normalized_matrix= adjMatrix.mmul(degree_matrix);



        return null;

    }




}
