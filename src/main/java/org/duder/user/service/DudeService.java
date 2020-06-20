package org.duder.user.service;

import org.duder.dto.user.Dude;

import java.util.List;

public interface DudeService {
    List<Dude> getDudes(int page, int size);
}
