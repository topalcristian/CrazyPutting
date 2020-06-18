package com.golf.game.Bot;

public class ExampleFactory implements NodeFactory {

    @Override
    public AbstractNode createNode(int x, int y) {
        return new Node(x, y);
    }

}

