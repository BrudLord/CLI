package io.cli.parsers;

import io.cli.command.Command;
import io.cli.parsers.innerparser.PipeParser;
import io.cli.parsers.innerparser.QouteParser;
import io.cli.parsers.innerparser.Substitutor;
import io.cli.parsers.token.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InputParser {
    List<Command> commands;

    public List<Command> parse(String str) {
        return tokenize(
                PipeParser.parsePipe(
                        Substitutor.substitute(
                                QouteParser.parseQoute(str)
                        )
                )
        );
    }

    private List<Command> tokenize(List<List<Token>> tokens) {
        return tokens.stream().map(
                tolen_lst -> commands.stream().map(
                        command -> command.newInstance(tolen_lst)
                ).findFirst().orElse(null)
        ).toList();
    }
}