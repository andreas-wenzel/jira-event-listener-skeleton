package ut.com.example.tutorial.plugins;

import org.junit.Test;
import com.example.tutorial.plugins.api.MyPluginComponent;
import com.example.tutorial.plugins.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest {
	@Test
	public void testMyName() {
		MyPluginComponent component = new MyPluginComponentImpl(null, null, null, null);
		assertEquals("names do not match!", "JIRA-EventListener-Plugin", component.getName());
	}
}