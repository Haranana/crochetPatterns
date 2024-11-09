package com.example.crochetPatterns;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Services {
    private Map<Integer , Post> dataTable = new HashMap<>(Map.of(
            0 , new Post("Temat1" , "Lorem Ipsum itp"),
            1 , new Post("Temat2" , "Jakis tam tekst")
    ));
    private Integer currentId = 2;

    public void addPost(Post newPost){
        dataTable.put(currentId , newPost);
        currentId++;
    }

    public void deletePost(int postId){
        dataTable.remove(postId);
    }

    public Post getPost(int postId){
        return dataTable.get(postId);
    }

    public Map<Integer , Post> getPosts(){
        return dataTable;
    }

}
