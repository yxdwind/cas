package org.apereo.cas.services;

import com.google.common.collect.Lists;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.cache.CachingPrincipalAttributesRepository;
import org.apereo.cas.util.SerializationUtils;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.apereo.services.persondir.IPersonAttributes;
import org.apereo.services.persondir.support.StubPersonAttributeDao;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Attribute filtering policy tests.
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class RegisteredServiceAttributeReleasePolicyTests {
    @Test
    public void verifyAttributeFilterMappedAttributes() {
        final ReturnMappedAttributeReleasePolicy policy = new ReturnMappedAttributeReleasePolicy();
        final Map<String, String> mappedAttr = new HashMap<>();
        mappedAttr.put("attr1", "newAttr1");
        
        policy.setAllowedAttributes(mappedAttr);
                
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Lists.newArrayList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), 1);
        assertTrue(attr.containsKey("newAttr1"));
        
        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnMappedAttributeReleasePolicy p2 =
            SerializationUtils.deserializeAndCheckObject(data, ReturnMappedAttributeReleasePolicy.class);
        assertNotNull(p2);
        assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
    }
    
    @Test
    public void verifyServiceAttributeFilterAllowedAttributes() {
        final ReturnAllowedAttributeReleasePolicy policy = new ReturnAllowedAttributeReleasePolicy();
        policy.setAllowedAttributes(Lists.newArrayList("attr1", "attr3"));
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Lists.newArrayList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), 2);
        assertTrue(attr.containsKey("attr1"));
        assertTrue(attr.containsKey("attr3"));
        
        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnAllowedAttributeReleasePolicy p2 =
            SerializationUtils.deserializeAndCheckObject(data, ReturnAllowedAttributeReleasePolicy.class);
        assertNotNull(p2);
        assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
    }

    
    @Test
    public void verifyServiceAttributeFilterAllAttributes() {
        final ReturnAllAttributeReleasePolicy policy = new ReturnAllAttributeReleasePolicy();
        final Principal p = mock(Principal.class);

        final Map<String, Object> map = new HashMap<>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Lists.newArrayList("v3", "v4"));

        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");

        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), map.size());

        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnAllAttributeReleasePolicy p2 =
            SerializationUtils.deserializeAndCheckObject(data, ReturnAllAttributeReleasePolicy.class);
        assertNotNull(p2);
    }

    @Test
    public void checkServiceAttributeFilterAllAttributesWithCachingTurnedOn() {
        final ReturnAllAttributeReleasePolicy policy = new ReturnAllAttributeReleasePolicy();

        final Map<String, List<Object>> attributes = new HashMap<>();
        attributes.put("values", Lists.newArrayList(new Object[]{"v1", "v2", "v3"}));
        attributes.put("cn", Lists.newArrayList(new Object[]{"commonName"}));
        attributes.put("username", Lists.newArrayList(new Object[]{"uid"}));

        final IPersonAttributeDao dao = new StubPersonAttributeDao(attributes);
        final IPersonAttributes person = mock(IPersonAttributes.class);
        when(person.getName()).thenReturn("uid");
        when(person.getAttributes()).thenReturn(attributes);

        final CachingPrincipalAttributesRepository repository =
                new CachingPrincipalAttributesRepository(TimeUnit.MILLISECONDS.name(), 100);
        repository.setAttributeRepository(dao);

        final Principal p = new DefaultPrincipalFactory().createPrincipal("uid",
                    Collections.<String, Object>singletonMap("mail", "final@example.com"));

        policy.setPrincipalAttributesRepository(repository);

        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), attributes.size());
    }
}
