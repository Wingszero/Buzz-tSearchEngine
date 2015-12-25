//package com.myapp.worker;
//
//import javax.naming.directory.Attributes;
//import javax.naming.directory.DirContext;
//import javax.naming.directory.InitialDirContext;
//import javax.naming.Context;
//import javax.naming.NamingEnumeration;
//import javax.naming.NamingException;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.Hashtable;
//
//public class DNSResolver
//{
//    public static void main(String args[])
//    {
//        String host = "google.com";
//        try
//        {
//            InetAddress inetAddress = InetAddress.getByName(host);
//            // show the Internet Address as name/address
//            System.out.println(inetAddress.getHostName() + " " + inetAddress.getHostAddress());
//
//            Hashtable<String, String> env = new Hashtable<String, String>();
//            //env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
//            //env.put(Context.PROVIDER_URL, "ldap://localhost:389/o=JNDITutorial");
//
//            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.dns.DnsContextFactory");
//            //env.put(Context.PROVIDER_URL, "dns://google.com");
//
//            // get the default initial Directory Context
//            InitialDirContext iDirC = new InitialDirContext(env);
//            // get the DNS records for inetAddress
//            Attributes attributes = iDirC.getAttributes("dns:/"+inetAddress.getHostName());
//            // get an enumeration of the attributes and print them out
//            NamingEnumeration<?> attributeEnumeration = attributes.getAll();
//            System.out.println("");
//            while (attributeEnumeration.hasMore())
//            {
//                System.out.println("" + attributeEnumeration.next());
//            }
//            attributeEnumeration.close();
//        }
//        catch (UnknownHostException exception)
//        {
//            System.err.println("ERROR: Cannot access '" + host + "'");
//        }
//        catch (NamingException exception)
//        {
//            System.err.println("ERROR: No DNS record for '" + host + "'");
//            exception.printStackTrace();
//        }
//    }
//}