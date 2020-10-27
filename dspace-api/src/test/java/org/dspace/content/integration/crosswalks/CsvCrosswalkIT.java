/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.integration.crosswalks;

import static org.dspace.builder.CollectionBuilder.createCollection;
import static org.dspace.builder.CommunityBuilder.createCommunity;
import static org.dspace.builder.ItemBuilder.createItem;
import static org.dspace.core.CrisConstants.PLACEHOLDER_PARENT_METADATA_VALUE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.dspace.AbstractIntegrationTestWithDatabase;
import org.dspace.app.util.DCInputsReader;
import org.dspace.app.util.DCInputsReaderException;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.utils.DSpace;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for the {@link CsvCrosswalkIT}.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public class CsvCrosswalkIT extends AbstractIntegrationTestWithDatabase {

    private static final String BASE_OUTPUT_DIR_PATH = "./target/testing/dspace/assetstore/crosswalk/";

    private Community community;

    private Collection collection;

    private StreamDisseminationCrosswalkMapper crosswalkMapper;

    private DCInputsReader dcInputsReader;

    @Before
    public void setup() throws SQLException, AuthorizeException, DCInputsReaderException {

        this.crosswalkMapper = new DSpace().getSingletonService(StreamDisseminationCrosswalkMapper.class);
        assertThat(crosswalkMapper, notNullValue());

        context.turnOffAuthorisationSystem();
        community = createCommunity(context).build();
        collection = createCollection(context, community).withAdminGroup(eperson).build();
        context.restoreAuthSystemState();

        dcInputsReader = mock(DCInputsReader.class);

        when(dcInputsReader.hasFormWithName("traditional-oairecerif-identifier-url")).thenReturn(true);
        when(dcInputsReader.getAllFieldNamesByFormName("traditional-oairecerif-identifier-url"))
            .thenReturn(Arrays.asList("oairecerif.identifier.url", "crisrp.site.title"));

        when(dcInputsReader.hasFormWithName("traditional-oairecerif-person-affiliation")).thenReturn(true);
        when(dcInputsReader.getAllFieldNamesByFormName("traditional-oairecerif-person-affiliation"))
            .thenReturn(Arrays.asList("oairecerif.person.affiliation", "oairecerif.affiliation.startDate",
                "oairecerif.affiliation.endDate", "oairecerif.affiliation.role"));

        when(dcInputsReader.hasFormWithName("traditional-crisrp-education")).thenReturn(true);
        when(dcInputsReader.getAllFieldNamesByFormName("traditional-crisrp-education"))
            .thenReturn(Arrays.asList("crisrp.education", "crisrp.education.start",
                "crisrp.education.end", "crisrp.education.role"));

        when(dcInputsReader.hasFormWithName("traditional-crisrp-qualification")).thenReturn(true);
        when(dcInputsReader.getAllFieldNamesByFormName("traditional-crisrp-qualification"))
            .thenReturn(Arrays.asList("crisrp.qualification", "crisrp.qualification.start",
                "crisrp.qualification.end"));

    }

    @Test
    public void testDisseminateManyPersons() throws Exception {

        context.turnOffAuthorisationSystem();

        Item firstItem = createFullPersonItem();

        Item secondItem = createItem(context, collection)
            .withTitle("Edward Red")
            .withGivenName("Edward")
            .withFamilyName("Red")
            .withBirthDate("1982-05-21")
            .withGender("M")
            .withPersonAffiliation("OrgUnit")
            .withPersonAffiliationStartDate("2015-01-01")
            .withPersonAffiliationRole("Developer")
            .withPersonAffiliationEndDate(PLACEHOLDER_PARENT_METADATA_VALUE)
            .build();

        Item thirdItem = createItem(context, collection)
            .withTitle("Adam White")
            .withGivenName("Adam")
            .withFamilyName("White")
            .withBirthDate("1962-03-23")
            .withGender("M")
            .withJobTitle("Researcher")
            .withPersonMainAffiliation("University of Rome")
            .withPersonKnowsLanguages("English")
            .withPersonKnowsLanguages("Italian")
            .withPersonEducation("School")
            .withPersonEducationStartDate("2000-01-01")
            .withPersonEducationEndDate("2005-01-01")
            .withPersonEducationRole("Student")
            .build();

        context.restoreAuthSystemState();

        CsvCrosswalk csvCrosswalk = (CsvCrosswalk) crosswalkMapper.getByType("person-csv");
        assertThat(csvCrosswalk, notNullValue());
        csvCrosswalk.setDCInputsReader(dcInputsReader);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        csvCrosswalk.disseminate(context, Arrays.asList(firstItem, secondItem, thirdItem).iterator(), out);

        try (FileInputStream fis = getFileInputStream("persons.csv")) {
            String expectedCsv = IOUtils.toString(fis, Charset.defaultCharset());
            assertThat(out.toString(), equalTo(expectedCsv));
        }
    }

    @Test
    public void testDisseminateSinglePerson() throws Exception {

        context.turnOffAuthorisationSystem();

        Item item = createItem(context, collection)
            .withTitle("Walter White")
            .withVariantName("Heisenberg")
            .withVariantName("W.W.")
            .withGivenName("Walter")
            .withFamilyName("White")
            .withBirthDate("1962-03-23")
            .withGender("M")
            .withJobTitle("Professor")
            .withPersonMainAffiliation("High School")
            .withPersonKnowsLanguages("English")
            .withPersonEducation("School")
            .withPersonEducationStartDate("1968-09-01")
            .withPersonEducationEndDate("1973-06-10")
            .withPersonEducationRole("Student")
            .withPersonEducation("University")
            .withPersonEducationStartDate("1980-09-01")
            .withPersonEducationEndDate("1985-06-10")
            .withPersonEducationRole("Student")
            .withOrcidIdentifier("0000-0002-9079-5932")
            .withPersonQualification("Qualification")
            .withPersonQualificationStartDate(PLACEHOLDER_PARENT_METADATA_VALUE)
            .withPersonQualificationEndDate(PLACEHOLDER_PARENT_METADATA_VALUE)
            .build();

        context.restoreAuthSystemState();

        CsvCrosswalk csvCrosswalk = (CsvCrosswalk) crosswalkMapper.getByType("person-csv");
        assertThat(csvCrosswalk, notNullValue());
        csvCrosswalk.setDCInputsReader(dcInputsReader);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        csvCrosswalk.disseminate(context, item, out);

        try (FileInputStream fis = getFileInputStream("person.csv")) {
            String expectedCsv = IOUtils.toString(fis, Charset.defaultCharset());
            assertThat(out.toString(), equalTo(expectedCsv));
        }
    }

    private Item createFullPersonItem() {
        Item item = createItem(context, collection)
            .withTitle("John Smith")
            .withFullName("John Smith")
            .withVernacularName("JOHN SMITH")
            .withVariantName("J.S.")
            .withVariantName("Smith John")
            .withGivenName("John")
            .withFamilyName("Smith")
            .withBirthDate("1992-06-26")
            .withGender("M")
            .withJobTitle("Researcher")
            .withPersonMainAffiliation("University")
            .withWorkingGroup("First work group")
            .withWorkingGroup("Second work group")
            .withPersonalSiteUrl("www.test.com")
            .withPersonalSiteTitle("Test")
            .withPersonalSiteUrl("www.john-smith.com")
            .withPersonalSiteTitle(PLACEHOLDER_PARENT_METADATA_VALUE)
            .withPersonalSiteUrl("www.site.com")
            .withPersonalSiteTitle("Site")
            .withPersonEmail("test@test.com")
            .withSubject("Science")
            .withOrcidIdentifier("0000-0002-9079-5932")
            .withScopusAuthorIdentifier("111")
            .withResearcherIdentifier("r1")
            .withResearcherIdentifier("r2")
            .withPersonAffiliation("Company")
            .withPersonAffiliationStartDate("2018-01-01")
            .withPersonAffiliationRole("Developer")
            .withPersonAffiliationEndDate(PLACEHOLDER_PARENT_METADATA_VALUE)
            .withDescriptionAbstract("Biography: \n\t\"This is my biography\"")
            .withPersonCountry("England")
            .withPersonKnowsLanguages("English")
            .withPersonKnowsLanguages("Italian")
            .withPersonEducation("School")
            .withPersonEducationStartDate("2000-01-01")
            .withPersonEducationEndDate("2005-01-01")
            .withPersonEducationRole("Student")
            .withPersonQualification("First Qualification")
            .withPersonQualificationStartDate("2015-01-01")
            .withPersonQualificationEndDate("2016-01-01")
            .withPersonQualification("Second Qualification")
            .withPersonQualificationStartDate("2016-01-02")
            .withPersonQualificationEndDate(PLACEHOLDER_PARENT_METADATA_VALUE)
            .build();
        return item;
    }

    private FileInputStream getFileInputStream(String name) throws FileNotFoundException {
        return new FileInputStream(new File(BASE_OUTPUT_DIR_PATH, name));
    }
}