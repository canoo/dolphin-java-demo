package org.opendolphin.demo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AddressGenerator extends AbstractValueGenerator<Address> {

    private static final Logger log  = Logger.getLogger(AddressGenerator.class.getName());

    private FirstNameGenerator firstNameGenerator = new FirstNameGenerator();
    private LastNameGenerator lastNameGenerator = new LastNameGenerator();
    private CityNameGenerator cityNameGenerator = new CityNameGenerator();

    private static final int START_ZIP = 1000;
    private static final int END_ZIP = 5000;

    private final List<String> cities = new ArrayList<String>();

    public List<Address> getAddressList(int numEntries) {
        log.info("initializing " + (END_ZIP - START_ZIP) + " cities...");
        for (int i = START_ZIP; i < END_ZIP; i++) {
            cities.add(i + " " + cityNameGenerator.randomValue());
        }
        List<Address> addressList = new ArrayList<Address>();
        log.info("initializing " + numEntries + " addresses...");
        for (int i=0; i < numEntries; i++) {
            addressList.add(randomValue());
        }
        return addressList;
    }

    @Override
    public Address randomValue() {
        Address address = new Address();
        address.setFirst(firstNameGenerator.randomValue());
        address.setLast(lastNameGenerator.randomValue());
        address.setCity(getRandomString(cities.toArray(new String[cities.size()])));
        address.setPhone(randomPhone());
        return address;
    }

    private String randomPhone() {
        int area = 100 + Double.valueOf(900 * Math.random()).intValue();
        int number = 1000 + Double.valueOf(9000 * Math.random()).intValue();
        return String.format("%s %s", area, number);
    }

    public static void main(String[] args) {
        AddressGenerator addressGenerator = new AddressGenerator();
        for (int i=0; i < 10000; i++) {
            System.out.println(addressGenerator.randomValue());
        }
    }
}
