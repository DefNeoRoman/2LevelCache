package com.example.LevelCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleService {

    @Autowired
    SimpleDataRepository repository;

    public void fillRepository(){

        repository.save(new SimpleData("ffg"));
        repository.save(new SimpleData("gfhhfgh"));
        repository.save(new SimpleData("dgffdg"));
        repository.save(new SimpleData("fghhf"));
        repository.save(new SimpleData("fhgfh"));
        repository.save(new SimpleData("dhghfgh"));
    }

    public List<SimpleData> getAll(){
        return repository.findAll();
    }

}
