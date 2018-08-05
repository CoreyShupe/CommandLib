package com.github.coreyshupe.command.expectations;

import com.github.coreyshupe.command.CommandFactory;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Ignore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;

/** @author CoreyShupe, created on 2018/08/05 */
@Ignore
public class CommandAnalyzer {
    private final ArrayDeque<String> futureCommands;
    private final ArrayDeque<String> futureExpectations;

    public CommandAnalyzer() {
        this.futureCommands = new ArrayDeque<>();
        this.futureExpectations = new ArrayDeque<>();
        try(var reader = new BufferedReader(new InputStreamReader(CommandAnalyzer.class.getResourceAsStream("/command_expectations")))) {
            String line;
            while((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] split = line.split(":");
                futureCommands.offerFirst(split[0]);
                futureExpectations.offerFirst(line.substring(split[0].length() + 1));
            }
        }catch(IOException ex) {
            Assertions.fail("Failed to read from command expectations.", ex);
        }
    }

    public void test(CommandFactory<Integer> commandFactory) {
        futureCommands.iterator().forEachRemaining(str -> commandFactory.publishRawContent(0, str));
    }

    public void analyzeNext(String result) {
        var expected = futureExpectations.poll();
        Assertions.assertThat(result).isEqualTo(expected);
    }
}
