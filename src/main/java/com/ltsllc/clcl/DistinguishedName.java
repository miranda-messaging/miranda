/*
 * Copyright  2017 Long Term Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ltsllc.clcl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * An LDAP Distinguished Name.
 *
 * <p>
 *     The class sets the Country Code, State, City, Company,
 *     and Division to {@link #UNKNOWN}, but the client must set the Name
 *     to some value.
 * </p>
 *
 * <h3>Attributes</h3>
 * <table width="1">
 *     <tr>
 *         <th>Name</th>
 *         <th>Type</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>countryCode</td>
 *         <td>String</td>
 *         <td>The 2-letter LDAP counrty code</td>
 *     </tr>
 *     <tr>
 *         <td>state</td>
 *         <td>String</td>
 *         <td>The state or province for the object</td>
 *     </tr>
 *     <tr>
 *         <td>city</td>
 *         <td>String</td>
 *         <td>The city ("locality" in LDAP speak) of the object</td>
 *     </tr>
 *     <tr>
 *         <td>company</td>
 *         <td>String</td>
 *         <td>The company or organization for the object</td>
 *     </tr>
 *     <tr>
 *         <td>division</td>
 *         <td>String</td>
 *         <td>The division within the company or organization of the object.  For example, "development".</td>
 *     </tr>
 *     <tr>
 *         <td>name</td>
 *         <td>String</td>
 *         <td>
 *             The "common name" of the person (if this object is for a person) or the
 *             fully qualified domain name (if this object is associated with a system)
 *             of the thing the object is associated with.
 *         </td>
 *     </tr>
 * </table>
 */
public class DistinguishedName {
    public static final String UNKNOWN = "Unknown";

    private String countryCode = UNKNOWN;
    private String state = UNKNOWN;
    private String city = UNKNOWN;
    private String company = UNKNOWN;
    private String division = UNKNOWN;

    private String name;

    public DistinguishedName() {
    }

    public DistinguishedName (DistinguishedName dn) {
        this.countryCode = dn.getCountryCode();
        this.state = dn.getState();
        this.city = dn.getCity();
        this.company = dn.getCompany();
        this.division = dn.getDivision();
        this.name = dn.getName();
    }

    public DistinguishedName (Principal principal) {
        initialize(principal.toString());
    }

    public DistinguishedName (String dn) {
        initialize(dn);
    }

    public void initialize (String dn) {
        String fields[] = dn.split(",");
        List<LDAPName> names = toLDAPNames(fields);

        setCountryCode(findCountryCode(names));
        setState(findState(names));
        setCity(findCity(names));
        setCompany(findCompany(names));
        setDivision(findDivision(names));
        setName(findName(names));
    }

    public static List<LDAPName> toLDAPNames (String[] names) {
        List<LDAPName> ldapNames = new ArrayList<LDAPName>();
        for (String name : names) {
            LDAPName aLdapName = new LDAPName(name);
            ldapNames.add(aLdapName);
        }

        return ldapNames;
    }

    public String findCountryCode(List<LDAPName> names) {
        return find ("c", names);
    }

    public String findState (List<LDAPName> names) {
        return find ("st", names);
    }

    public String findCity (List<LDAPName> names) {
        return find ("l", names);
    }

    public String findCompany (List<LDAPName> names) {
        return find ("o", names);
    }

    public String findDivision (List<LDAPName> names) {
        return find ("ou", names);
    }

    public String findName (List<LDAPName> names) {
        return find ("cn", names);
    }

    public String find (String key, List<LDAPName> names) {
        for (LDAPName ldapName : names) {
            if (ldapName.getKey().equalsIgnoreCase(key))
                return ldapName.getValue();
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getCompany() {

        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCity() {

        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {

        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountryCode() {

        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Create a string, suitable for use as an {@link sun.security.x509.X500Name}
     * for the object.
     *
     * @return The string described above.
     */
    public String toString () {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("c=");
        stringBuilder.append(getCountryCode());
        stringBuilder.append(",st=");
        stringBuilder.append(getState());
        stringBuilder.append(",l=");
        stringBuilder.append(getCity());
        stringBuilder.append(",o=");
        stringBuilder.append(getCompany());
        stringBuilder.append(",ou=");
        stringBuilder.append(getDivision());
        stringBuilder.append(",cn=");
        stringBuilder.append(getName());

        return stringBuilder.toString();
    }

    public boolean equals (Object o) {
        if (null == o || !(o instanceof DistinguishedName))
            return false;

        DistinguishedName other = (DistinguishedName) o;
        if (!getCountryCode().equals(other.getCountryCode()))
            return false;

        if (!getState().equals(other.getState()))
            return false;

        if (!getCity().equals(other.getCity()))
            return false;

        if (!getCompany().equals(other.getCompany()))
            return false;

        if (!getDivision().equals(other.getDivision()))
            return false;

        if (!getName().equals(other.getName()))
            return false;

        return true;
    }
}
