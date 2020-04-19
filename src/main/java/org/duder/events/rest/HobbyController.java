package org.duder.events.rest;

import org.duder.events.dao.Hobby;
import org.duder.events.model.HobbyName;
import org.duder.events.repository.HobbyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hobby")
public class HobbyController {
    private final HobbyRepository hobbyRepository;

    public HobbyController(HobbyRepository hobbyRepository) {
        this.hobbyRepository = hobbyRepository;
    }

    @GetMapping
    public List<HobbyName> getHobbyTypes() {
        return hobbyRepository.findAll().stream().map(Hobby::getName).collect(Collectors.toList());
    }
}
