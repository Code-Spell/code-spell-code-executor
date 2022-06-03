package pt.ua.deti.codespell.utils;

import lombok.Data;

import java.util.List;

@Data
public class Step {

    private final int id;
    private final boolean successful;
    private final List<String> args;

}
