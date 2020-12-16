package com.company;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.Random;

public class Lab4
{


    public Lab4()
    {

        BigInteger q = BigInteger.probablePrime(16, new SecureRandom());
        BigInteger N = BigInteger.TWO.multiply(q).add(BigInteger.ONE);

        BigInteger g = BigInteger.valueOf(2);
        BigInteger k = new BigInteger("3");

        System.out.println("Сгенерировно :\n\tN = " + N + "\n\tg = " + g + "\n\tk = " + k);

        SPR_6_Server server = new SPR_6_Server(N, g, k);

        String username = "Name";
        String password = "pa2020";

        System.out.println("\nСоздан Клиент : \n\tusername = " + username + "\n\tpassword = " + password);
        SPR_6_Client client = new SPR_6_Client(N, g, k, username, password, server);

        try
        {
            client.sendRegistration();
            client.sendLogin();
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }


    private static String bytesToHex(byte[] hash)
    {
        StringBuilder hexString = new StringBuilder();

        for (byte hash_byte : hash) {
            String hex = Integer.toHexString(0xff & hash_byte);

            if (hex.length() == 1)
                hexString.append('0');

            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static BigInteger hash(Object... input)
    {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            for (Object i : input) {
                if (i instanceof String)
                    sha256.update(((String) i).getBytes());
                else if (i instanceof BigInteger)
                    sha256.update(((BigInteger) i).toString(10).getBytes());
                else if (i instanceof byte[])
                    sha256.update((byte[]) i);
                else
                    throw new IllegalArgumentException();
            }
            return new BigInteger(bytesToHex(sha256.digest()), 16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return BigInteger.ZERO;
        }


    }
}

class SPR_6_Server
{

    private final BigInteger k;
    private final BigInteger N;
    private final BigInteger g;



    private Person person;


    public SPR_6_Server(BigInteger N, BigInteger g, BigInteger k)
    {
        this.N = N;
        this.g = g;
        this.k = k;


    }


    public void processRegistration(String username, String salt, BigInteger v)
    {
        String saltNew = salt.replace("\n", "\\n");
        System.out.println("Сервер получил : username = " + username + "\tsalt =" + saltNew + "\tv = " + v);

        person = new Person(username, salt, v);
    }


    public void processLogin(SPR_6_Client client, String username, BigInteger A) throws Exception
    {
        if(!A.equals(BigInteger.ZERO))
        {
            if (person.username.compareTo(username) == 0)
            {
                System.out.println("Сервер получил : username = " + username + "\tA = " + A);

                person.setA(A);
                person.set_b(generateB());
                person.setB(generateBackAuthentication(person.v, person.b));

                String salt = person.salt.replace("\n", "\\n");
                System.out.println("\nСервер сгенерировал :\n\tb = " + person.b + "\n\tB = " + person.B);
                System.out.println("\nСервер -> Клиенту : salt = " + salt + "\tB = " + person.B);
                client.receiveLoginRequest(person.salt, person.B);

                person.setU(generateU(person.A, person.B));
                person.setK(generateKey(person.A, person.v, person.b, person.u));

                System.out.println("\nСервер сгенерировал generated : \n\tu = " + person.u + "\n\tK = " + person.K);

                BigInteger clientM = receiveFinalKey(client.sendFinalKey());

                System.out.println("Сервер получил : finalKey = " + clientM);

                person.setM(generateM(person.username, person.salt, person.A, person.B, person.K));

                System.out.println("\nСервер сгенерировал : \n\tfinalKey = " + person.M);

                if (clientM.equals(person.M))
                {
                    System.out.println("\nСервер подтвердил идентичность финальных клчючей");

                    BigInteger request = generateRequest(person.A, person.M, person.K);

                    System.out.println("\nСервер сгенерировал : \n\trequest = " + request);
                    System.out.println("\nСервер -> Клиенту : request = " + request);

                    client.receiveConfirmationRequest(request);
                } else
                    throw new Exception("Ошибка, финальные ключи не совпадают");
            }
        }
        else
            throw new Exception("Ошибка, A == 0");

    }

    private BigInteger receiveFinalKey(BigInteger M) { return M; }


    private BigInteger generateM(String username, String salt, BigInteger authentication, BigInteger back_authentication, BigInteger K) { return Lab4.hash(Lab4.hash(this.N).xor(Lab4.hash(this.g)), Lab4.hash(username), salt, authentication, back_authentication, K); }
    private BigInteger generateKey(BigInteger authentication, BigInteger verifier, BigInteger b, BigInteger u) { return Lab4.hash(authentication.multiply(verifier.modPow(u, N)).modPow(b, N)); }
    public BigInteger generateRequest(BigInteger authentication, BigInteger M, BigInteger K) { return Lab4.hash(authentication, M, K); }
    private BigInteger generateB() { return new BigInteger(512, new Random()); }
    public BigInteger generateBackAuthentication(BigInteger currentPersonVerifier, BigInteger b) { return (this.k.multiply(currentPersonVerifier).add(this.g.modPow(b, this.N))).mod(this.N); }


    public BigInteger generateU(BigInteger authentication, BigInteger currentPersonBackAuthentication) throws Exception
    {
        BigInteger u = Lab4.hash(authentication, currentPersonBackAuthentication);

        if (u.equals(BigInteger.ZERO))
            throw new Exception("Ошбика, u == 0");
        else
            return u;
    }


    static class Person
    {
        private final String username;
        private final String salt;

        private final BigInteger v;
        private BigInteger A;
        private BigInteger B;

        private BigInteger b;
        private BigInteger u;
        private BigInteger K;
        private BigInteger M;

        public void setK(BigInteger k) { this.K = k; }
        public void setM(BigInteger m) { this.M = m; }
        public void setU(BigInteger u) { this.u = u; }
        public void set_b(BigInteger b) { this.b = b; }

        public void setA(BigInteger a) { this.A = a; }
        public void setB(BigInteger b) { this.B = b; }


        public Person(String username, String salt, BigInteger v)
        {
            this.username = username;
            this.salt = salt;
            this.v = v;
        }
    }
}

class SPR_6_Client
{
    private final BigInteger N;
    private final BigInteger g;
    private final BigInteger k;

    private BigInteger v;

    private final String username;
    private final String password;
    private String salt;

    private BigInteger A;
    private BigInteger a;


    private BigInteger x;
    private BigInteger u;


    private BigInteger B;
    private BigInteger M;
    private BigInteger K;


    private final SPR_6_Server server;


    public SPR_6_Client(BigInteger N, BigInteger g, BigInteger k, String username, String password, SPR_6_Server server)
    {
        this.N = N;
        this.g = g;
        this.k = k;

        this.username = username;
        this.password = password;

        this.server = server;

        fillBaseData();
    }


    public String generateSalt()
    {
        StringBuilder s = new StringBuilder();

        for(int i = 0 ; i < 10 + Math.random() * 30 ; i++)
            s.append((char) (0 + Math.random() * 255));

        return s.toString();
    }

    public void sendRegistration()
    {
        String salt = this.salt.replace("\n", "\\n");
        System.out.println("\nКлиент -> Серверу : username = " + this.username + "\tsalt = " + salt + "\tv = " + this.v);
        server.processRegistration(this.username, this.salt, this.v);
    }

    private void fillBaseData()
    {

        salt = generateSalt();
        x = Lab4.hash(salt, password);
        v = g.modPow(x, N);

        String salt = this.salt.replace("\n", "\\n");
        System.out.println("\nКлиент сгенерировал :\n\tsalt = " + salt + "\n\tv = " + this.v);
    }


    public void sendLogin() throws Exception
    {
        generateA();
        generateAuthentication();
        System.out.println("\nКлиент сгенерировал :\n\ta = " + this.a + "\n\tA = " + this.A);
        System.out.println("\nКлиент -> Серверу : username = " + this.username + "\tA = " + this.A);
        server.processLogin(this, this.username, this.A);
    }


    public void receiveLoginRequest(String salt, BigInteger backAuthentication) throws Exception
    {
        if(salt.compareTo(this.salt) == 0)
        {
            if(!backAuthentication.equals(BigInteger.ZERO))
            {
                this.B = backAuthentication;
                generateU(backAuthentication);
                generateKey(backAuthentication);

                salt = salt.replace("\n", "\\n");
                System.out.println("Клиент получил : salt = " + salt + "\tB = " + backAuthentication);
                System.out.println("\nКлиент сгенерировал : \n\tu = " + this.u + "\n\tK = " + this.K);

            }
            else
                throw new Exception("Error, back_authentication is ZERO");
        }
        else
            throw new Exception("Error salt doesn't match");


    }

    public BigInteger sendFinalKey()
    {
        this.M = generateFinalKey();
        System.out.println("\nКлиент сгенерировал : \n\tfinalKey = " + this.M);
        System.out.println("\nКлиент -> Серверу : finalKey = " + this.M);

        return this.M;
    }




    public void receiveConfirmationRequest(BigInteger request) throws Exception
    {
        if(request.equals(Lab4.hash(A, M, K)))
        {
            System.out.println("Клиент получил : request = " + request);
            System.out.println("\nУспешное завершение!");
        }
        else
            throw new Exception("Ошибка, R не совпадает");
    }




    public void generateU(BigInteger back_authentication) throws  Exception
    {
        u = Lab4.hash(A, back_authentication);
        if (u.equals(BigInteger.ZERO))
            throw new Exception();
    }


    public void generateAuthentication() { A = g.modPow(a, N); }
    private void generateA() { a = new BigInteger(512, new Random()); }

    public void generateKey(BigInteger back_authentication) { K = Lab4.hash((back_authentication.subtract(k.multiply(g.modPow(x, N)))).modPow(a.add(u.multiply(x)), N)); }

    private BigInteger generateFinalKey() { return Lab4.hash(Lab4.hash(N).xor(Lab4.hash(g)), Lab4.hash(username), salt, A, B, K); }
}
