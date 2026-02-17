package com.shaibachar.calc;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TestMe {
    private final Date m_time;
    private final String m_name;
    private final List<Long> m_numbers;
    private final List<String> m_strings;

    public TestMe(Date time, String name, List<Long> numbers, List<String> strings) {
        m_time = new Date(time.getTime());
        m_name = name;
        m_numbers = new ArrayList<>(numbers);
        m_strings = new ArrayList<>(strings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_time, m_name, m_numbers, m_strings);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestMe testMe = (TestMe) o;
        return Objects.equals(m_time, testMe.m_time)
                && Objects.equals(m_name, testMe.m_name)
                && Objects.equals(m_numbers, testMe.m_numbers)
                && Objects.equals(m_strings, testMe.m_strings);
    }

    // FIX - replace with String
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (long item : m_numbers) {
            sb.append(' ').append(item);
        }
        return sb.toString();
    }

    public void removeStringBad(String str) {
        m_strings.removeIf(str::equals);
    }

    public boolean containsNumber(long number) {
        return m_numbers.contains(number);
    }

    public boolean containsNumberBad(long number) {
        for (long num : m_numbers) {
            if (num == number) {
                return true;
            }
        }
        return false;
    }

    // FIX - remove the new Date
    public boolean isHistoric() {
        return m_time.toInstant().isBefore(Instant.now());
    }

    public List<String> getM_strings() {
        return new ArrayList<>(m_strings);
    }

    public static void main(String[] args) {
        List<Long> numbers = new ArrayList<>();
        numbers.add(1L);
        numbers.add(2L);
        numbers.add(3L);

        List<String> strings = new ArrayList<>();
        strings.add("Hello");
        strings.add("World");

        TestMe test = new TestMe(new Date(), "Test", numbers, strings);
        test.removeStringBad("Hello");
        System.out.println(test);
        System.out.println(test.getM_strings());
    }
}