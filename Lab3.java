package com.company;

import java.math.BigInteger;
import java.security.SecureRandom;


public class Lab3
{
    private  final BigInteger[] keys;

    Lab3()
    {
        keys = generateRSAKeys();
        System.out.println("Сгенерированы ключи : \n\te = " + keys[0] + "\n\td = " + keys[1] + "\n\tn = " + keys[2]);

        String message = "Сегодня 17 07 число __!";

        System.out.println("Базовое сообщение : " + message);

        message = encrypt(message);

        System.out.println("Защифрованное сообщение : " + message);

        message = decrypt(message);

        System.out.println("Расшифрованное сообщение : " + message);


    }


    private String encrypt(String message)
    {
        StringBuilder newMessage = new StringBuilder();

        for(int i = 0 ; i < message.length() ; i ++)
            newMessage.append(BigInteger.valueOf(message.charAt(i)).modPow(keys[0], keys[2]).toString()).append(' ');

        return newMessage.toString();
    }


    private String decrypt(String message)
    {
        String[] messageReceived = message.split(" ");
        StringBuilder newMessage = new StringBuilder();

        for(String s : messageReceived)
            newMessage.append((char) new BigInteger(s).modPow(keys[1], keys[2]).intValue());

        return newMessage.toString();
    }

    public BigInteger[] generateRSAKeys()
    {
        BigInteger[] primes = generateSafePrimes();
        BigInteger n = generateN(primes);

        BigInteger[] buffer = generateE(primes);
        BigInteger d = generateD(buffer);

        return new BigInteger[]{ buffer[1], d, n };
    }

    private BigInteger[] generateSafePrimes()
    {
        BigInteger[] primes = {BigInteger.ZERO, BigInteger.ZERO};
        BigInteger buffer;

        SecureRandom random = new SecureRandom();

        while (primes[0].equals(BigInteger.ZERO) || primes[1].equals(BigInteger.ZERO))
        {
            buffer = BigInteger.probablePrime(8, random);

            if(primes[0].equals(BigInteger.ZERO))
                primes[0] = buffer;
            else if(primes[1].equals(BigInteger.ZERO))
                if(!primes[0].equals(buffer))
                    primes[1] = buffer;
                else if(primes[0].equals(BigInteger.ZERO) && primes[1].equals(BigInteger.ZERO))
                    break;
        }

        return primes;
    }


    private BigInteger[] generateE(BigInteger[] primes)
    {
        SecureRandom random = new SecureRandom();

        BigInteger[] service = new BigInteger[2];
        service[0] = (primes[0].subtract(BigInteger.ONE)).multiply((primes[1].subtract(BigInteger.ONE)));

        do { service[1] = BigInteger.probablePrime(8, random); }
        while (service[0].mod(service[1]).equals(BigInteger.ZERO));

        return service;
    }


    private BigInteger generateD(BigInteger[] service)
    {
        BigInteger i = BigInteger.TWO;
        for(;;)
        {
            if( service[1].multiply(i).mod(service[0]).equals(BigInteger.ONE))
                return i;

            i = i.add(BigInteger.ONE);
        }
    }

    private BigInteger generateN(BigInteger[] primes) { return primes[0].multiply(primes[1]); }
}
