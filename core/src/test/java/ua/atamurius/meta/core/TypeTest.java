package ua.atamurius.meta.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.hamcrest.CoreMatchers.*;

public class TypeTest {

    Model model;
    Type a, b, c, d;

    @Before
    public void init() {
        model = new Model();
        a = model.createType();
        a.setName("A");

        b = model.createType();
        b.setName("B");

        c = model.createType();
        c.setName("C");

        d = model.createType();
        d.setName("D");
    }

    @Test
    public void testSetGroup() throws Exception {
        assumeThat(a.getName(), is("A"));
        a.setGroup("some.group");
        assertThat(a.getGroup(), is("some.group"));
        assertThat(a.getQualifiedName(), is("some.group:A"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonUniqName() {
        assumeThat(a.getQualifiedName(), is(":A"));
        b.setName("A");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonUniqNameInGroup() {
        a.setGroup("test");
        assumeThat(a.getQualifiedName(), is("test:A"));
        b.setName("A");
        b.setGroup("test");
    }

    @Test
    public void testNonUniqNameInOtherGroup() {
        a.setGroup("test");
        assumeThat(a.getQualifiedName(), is("test:A"));
        b.setName("A");
        assertThat(b.getQualifiedName(), is(":A"));
    }

    @Test
    public void testSetName() throws Exception {
        assumeThat(a.getGroup(), is(""));
        a.setName("AType");
        assertThat(a.getName(), is("AType"));
        assertThat(a.getQualifiedName(), is(":AType"));
    }

    //  |      A
    //  |   B     C
    //  V      D
    @Test
    public void testDeriveFrom() throws Exception {
        d.deriveFrom(c);
        assertTrue(d.isDerivedFrom(c));
        assertTrue(c.getSubtypes().contains(d));

        b.deriveFrom(a);
        assertTrue(b.isDerivedFrom(a));
        assertTrue(a.getSubtypes().contains(b));

        c.deriveFrom(a);
        assertTrue(c.isDerivedFrom(a));
        assertTrue(a.getSubtypes().contains(c));
        assertTrue(d.isDerivedFrom(a));
        assertFalse(a.getSubtypes().contains(d));

        d.deriveFrom(b);
        assertTrue(d.isDerivedFrom(b));
        assertTrue(b.getSubtypes().contains(d));
        assertTrue(d.isDerivedFrom(a));
        assertFalse(a.getSubtypes().contains(d));
    }

    @Test(expected = IllegalArgumentException.class)
    public void circularDerivation() {
        d.deriveFrom(c);
        c.deriveFrom(a);
        a.deriveFrom(d);
    }

    @Test
    public void testUnderiveFrom() throws Exception {
        // d -> c -> a
        d.deriveFrom(c);
        c.deriveFrom(a);
        assumeTrue(d.isDerivedFrom(a));
        c.underiveFrom(a);
        assertFalse(c.isDerivedFrom(a));
        assertFalse(d.isDerivedFrom(a));
    }

    @Test
    public void testAddAttribute() throws Exception {
        // c -> b -> a
        b.deriveFrom(a);
        Attribute test = a.addAttribute();
        test.setName("test");

        assertThat(a.getAttributes(), hasItem(test));
        assertThat(a.getAllAttributes(), hasItem(test));

        assertTrue(b.isDerivedFrom(a));
        assertThat(b.getAllAttributes(), hasItem(test));
        assertThat(b.getAttributes(), not(hasItem(test)));

        c.deriveFrom(b);
        assertThat(b.getAllAttributes(), hasItem(test));
        assertThat(b.getAttributes(), not(hasItem(test)));

        assertTrue(c.isDerivedFrom(a));
        assertThat(c.getAllAttributes(), hasItem(test));
        assertThat(c.getAttributes(), not(hasItem(test)));

        test.dispose();
        assertThat(a.getAttributes(), not(hasItem(test)));
        assertThat(a.getAllAttributes(), not(hasItem(test)));
        assertThat(b.getAttributes(), not(hasItem(test)));
        assertThat(b.getAllAttributes(), not(hasItem(test)));
        assertThat(c.getAttributes(), not(hasItem(test)));
        assertThat(c.getAllAttributes(), not(hasItem(test)));
    }
}









