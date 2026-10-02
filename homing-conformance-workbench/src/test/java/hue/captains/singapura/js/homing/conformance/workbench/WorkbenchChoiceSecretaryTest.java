package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.ssjs.test.SecretaryTestBase;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The workbench party, headless: a pick told to all and once, a late member answered alone, an opening told to all. */
class WorkbenchChoiceSecretaryTest extends SecretaryTestBase {

    private static final String DIR = "/homing/js/hue/captains/singapura/js/homing/conformance/workbench/";

    @BeforeEach
    void load() { loadSecretary(DIR + "WorkbenchChoiceSecretaryModule.js", "WorkbenchChoiceSecretary"); }

    @Test
    void aPickIsToldToEveryMember_once() {
        Value step = dispatch(initial(), envelope("Pick", Map.of("to", "module:a.B"), "navigator"));
        assertActionCount(step, 1);
        assertActionKind(step, 0, "BroadcastToMembers");
        assertEquals("Picked", action(step, 0).getMember("message").getMember("kind").asString());
        assertStateField(step, "picked", "module:a.B");
        assertActionCount(dispatch(step.getMember("newState"), envelope("Pick", Map.of("to", "module:a.B"), "navigator")), 0);
    }

    @Test
    void aLateMemberIsAnsweredAlone_whenSomethingIsPicked() {
        assertActionCount(dispatch(initial(), envelope("CurrentRequested", Map.of(), "summary")), 0);
        Value picked = dispatch(initial(), envelope("Pick", Map.of("to", "crate:x"), "navigator")).getMember("newState");
        Value step = dispatch(picked, envelope("CurrentRequested", Map.of(), "summary"));
        assertActionKind(step, 0, "SendToMember");
        assertEquals("summary", action(step, 0).getMember("to").asString());
    }

    @Test
    void anOpeningIsToldToEveryMember_everyTime() {
        Value step = dispatch(initial(), envelope("Open", Map.of("to", "module:a.B"), "navigator"));
        assertActionKind(step, 0, "BroadcastToMembers");
        assertEquals("Opening", action(step, 0).getMember("message").getMember("kind").asString());
        assertActionCount(dispatch(step.getMember("newState"), envelope("Open", Map.of("to", "module:a.B"), "navigator")), 1);
    }
}
