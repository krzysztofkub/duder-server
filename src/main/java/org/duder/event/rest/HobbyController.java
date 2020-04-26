package org.duder.event.rest;

import ord.duder.dto.event.HobbyName;
import org.duder.event.dao.Hobby;
import org.duder.event.repository.HobbyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hobby")
class HobbyController {
    private final HobbyRepository hobbyRepository;

    public HobbyController(HobbyRepository hobbyRepository) {
        this.hobbyRepository = hobbyRepository;
    }

    @GetMapping
    public List<HobbyName> getHobbyTypes() {
        return hobbyRepository.findAll().stream().map(Hobby::getName).collect(Collectors.toList());
    }
}
