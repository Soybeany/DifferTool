package differ;

import differ.util.FastStruct;
import org.junit.Test;
import tree.SimpleTree;
import tree.node.TreeNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * <br>Created by Soybeany on 2019/9/10.
 */
public class TreeImpl implements IDifferUtil {

    private static String textA = "what is that thing, what about that guy, who are you?";
    private static String textB = "what can I do for you? is that guy talking about you?";
    private static String textC = "what 8是456.7can I do for you? is that guy talking about you?";
    private static String textD = "what is that thing, and what is those guys, also what is the one, good what is this.";

    @Test
    public void testGroup() throws IOException {
        FastStruct groupA = new FastStruct(loadFile(), true);
//        FastStruct groupA = new FastStruct(textD, false);
//        List<Map<String, List<Unit>>> results = FastStructUtils.toAssistUnits(groupA);
//        System.out.println(results.size());
//        analyse(groupA);
        int a = 1;
    }


    @Test
    public void testTree() {
        SimpleTree tree = new SimpleTree();
        TreeNode node = new TreeNode(5);
        addNodes(tree,
                node,
                new TreeNode(7),
                new TreeNode(3),
                new TreeNode(2),
                new TreeNode(9),
                new TreeNode(4),
                new TreeNode(1)
        );
        tree.removeNode(node);
        List<TreeNode> nodes = tree.traverseInLDR();
        System.out.println(nodes);
    }

    private static String loadFile() throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("D:\\test.txt")))) {
            String line;
            while (null != (line = reader.readLine())) {
                builder.append(line).append("\n");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    private static void addNodes(SimpleTree tree, TreeNode... nodes) {
        for (TreeNode node : nodes) {
            tree.addNode(node);
        }
    }

    @Override
    public String[] showDiffer(String input1, String input2) {
        return new String[0];
    }
}
