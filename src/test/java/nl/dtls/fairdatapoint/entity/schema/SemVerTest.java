package nl.dtls.fairdatapoint.entity.schema;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class SemVerTest {

    @Test
    public void testSemVerString() {
        assertThat(new SemVer("1.2.3").toString(), is(equalTo("1.2.3")));
        assertThat(new SemVer("1.20.0").toString(), is(equalTo("1.20.0")));
        assertThat(new SemVer("0.1.0").toString(), is(equalTo("0.1.0")));
    }

    @Test
    public void testCompareBasic() {
        assertThat((new SemVer("1.2.3")).compareTo(new SemVer("1.2.3")), is(equalTo(0)));
        assertThat((new SemVer("1.0.0")).compareTo(new SemVer("1.0.5")), is(equalTo(-1)));
        assertThat((new SemVer("1.0.10")).compareTo(new SemVer("1.0.0")), is(equalTo(1)));
        assertThat((new SemVer("1.0.0")).compareTo(new SemVer("1.5.0")), is(equalTo(-1)));
        assertThat((new SemVer("1.10.574")).compareTo(new SemVer("1.0.999")), is(equalTo(1)));
        assertThat((new SemVer("1.18.0")).compareTo(new SemVer("2.5.0")), is(equalTo(-1)));
        assertThat((new SemVer("2.0.0")).compareTo(new SemVer("1.755.999")), is(equalTo(1)));
        assertThat((new SemVer("1.2.50")).compareTo(new SemVer("1.20.0")), is(equalTo(-1)));
        assertThat((new SemVer("1.20.2")).compareTo(new SemVer("1.2.50")), is(equalTo(1)));
    }

    @Test
    public void testCompareNull() {
        assertThat((new SemVer("1.2.3")).compareTo(null), is(equalTo(1)));
        assertThat((new SemVer("5.7.10")).compareTo(null), is(equalTo(1)));
    }

    @Test
    public void testSorting() {
        SemVer a = new SemVer("0.8.1");
        SemVer b = new SemVer("1.20.1");
        SemVer c = new SemVer("5.3.0");
        SemVer d = new SemVer("5.3.1");
        List<SemVer> lst = Arrays.asList(b, a, d, c);
        lst.sort(SemVer::compareTo);
        assertThat(lst.get(0), is(equalTo(a)));
        assertThat(lst.get(1), is(equalTo(b)));
        assertThat(lst.get(2), is(equalTo(c)));
        assertThat(lst.get(3), is(equalTo(d)));
    }
}
