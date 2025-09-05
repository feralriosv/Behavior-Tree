package view.configuration.loader;

import model.decisiontree.DecisionTree;
import model.decisiontree.node.Node;
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

    private boolean actionCreated;

    @Override
    public DecisionTree load(List<String> lines) {
        MermaidData mermaidData = new MermaidLoader().load(lines);

        if (mermaidData.isInvalid()) {
            return DecisionTree.unplayableTree();
        }

        this.actionCreated = false;
        TreeAssembler assembler = new TreeAssembler(mermaidData, this);
        Optional<Node<?>> rootNodeOpt = assembler.assemble();

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

