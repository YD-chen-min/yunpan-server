package com.yandan.yunstorage.VO;

import lombok.Data;

import java.util.List;

/**
 * Create by yandan
 * 2022/1/4  17:49
 */
@Data
public class Node {
    private String label;
    private List<Node> children;
    public Node(){

    }
    public Node(String label){
        this.label=label;
    }
}
