package view.configuration.loader;

import model.decisiontree.DecisionTree;
import model.decisiontree.node.Node;
import view.NodeFabric;
import view.configuration.mermaid.MermaidData;
import view.configuration.mermaid.MermaidLoader;
import view.configuration.mermaid.TreeAssembler;

import java.util.List;
import java.util.Optional;

/**
 * Loads a behavior/decision tree from Mermaid "flowchart TD" text.
 *
 * @author ubpst
 */
public class TreeLoader implements Loader<DecisionTree>, LoadCallBack {

    private final NodeFabric fabric;
    private final MermaidLoader loader;
    private boolean actionCreated;

    /**
     * Creates a new {@code TreeLoader} with default factories.
     */
    public TreeLoader() {
        this.fabric = new NodeFabric(this);
        this.loader = new MermaidLoader();
    }

    @Override
    public DecisionTree load(List<String> lines) {
        MermaidData mermaidData = loader.load(lines);


        if (mermaidData.isInvalid()) {
            return DecisionTree.unplayableTree();
        }

        this.actionCreated = false;
        TreeAssembler assembler = new TreeAssembler(this.fabric);
        Optional<Node<?>> rootNodeOpt = assembler.assemble(mermaidData, this);

        if (!actionCreated) {
            return DecisionTree.unplayableTree();
        }

        if (rootNodeOpt.isPresent()) {
            return new DecisionTree(rootNodeOpt.get());
        } else {
            return DecisionTree.unplayableTree();
        }
    }

    @Override
    public void markCreatedAction() {
        this.actionCreated = true;
    }
}

