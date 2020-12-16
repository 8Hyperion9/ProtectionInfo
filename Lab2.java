package com.company;

public class Lab2
{


    Lab2()
    {
        int firstKey = (int) (20 + Math.random() * 100);
        int secondKey = (int) (20 + Math.random() * 100);

        System.out.println("Сгенерированы публичные числа : " + firstKey + "  " + secondKey + '\n');

        Person first = new Person("Us1111", firstKey, secondKey);
        Person second = new Person("SECDI",firstKey, secondKey);

        first.receiveSendKey(second.sendSentKey(), second.sendName());
        second.receiveSendKey(first.sendSentKey(), first.sendName());


        first.createFinalEncryptionKey();
        second.createFinalEncryptionKey();

        first.printFinalEncryptionKey();
        second.printFinalEncryptionKey();
    }
}


class Person
{

    private final int[] publicKeys;
    private int secretKey;

    private int myIntermediateKey;
    private int opponentIntermediateKey;

    private int finalEncryptionKey;

    private final String name;

    Person(String name, int publicKeyFirst, int publicKeySecond)
    {
        this.name = name;

        this.publicKeys = new int[2];
        this.publicKeys[0] = publicKeyFirst;
        this.publicKeys[1] = publicKeySecond;

        createSecretKey();
        System.out.println("'" + this.name + "' сгенерирован секретный ключ = " + this.secretKey);

        createSentKey();
        System.out.println("'" + this.name + "' сгенерирован промежуточный ключ = " + this.myIntermediateKey + '\n');
    }

    private void createSecretKey() { this.secretKey = (int) (1 + Math.random() * 10); }

    private void createSentKey() { this.myIntermediateKey = (int) (Math.pow(publicKeys[0], secretKey) % publicKeys[1]); }


    public void createFinalEncryptionKey() { this.finalEncryptionKey = (int) (Math.pow(opponentIntermediateKey, secretKey) % publicKeys[1]); }

    public void printFinalEncryptionKey() {  System.out.println("\n'" + this.name + "'создан финальный ключ = " + this.finalEncryptionKey); }

    public int sendSentKey () { return this.myIntermediateKey; }

    public void receiveSendKey(int opponentIntermediateKey, String name)
    {
        this.opponentIntermediateKey = opponentIntermediateKey;
        System.out.println("'" + this.name + "' получен промежуточный ключ собеседника = " + opponentIntermediateKey + " от '" + name + "'");
    }

    public String sendName() { return this.name; }
}
