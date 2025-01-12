package uk.gov.justice.digital.delius.info;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HealthInfoTest {

    private HealthInfo healthInfo;

    @BeforeEach
    public void setUp() throws Exception {
        healthInfo = new HealthInfo();
    }

    @Test
    public void shouldIncludeVersionInfo() throws Exception {
        Assertions.assertThat(healthInfo.health().getDetails()).containsKey("version");
    }
}
