package capers;

import org.junit.Test;

import java.io.IOException;

public class test {
    @Test
    public void testWriteStory(){
        try {
            CapersRepository.setupPersistence();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CapersRepository.writeStory("test1");
        CapersRepository.writeStory("test2");
    }
    @Test
    public void testSaveAndReadDog(){
        CapersRepository.makeDog("Fido", "dalmation", 3);
    }
}
