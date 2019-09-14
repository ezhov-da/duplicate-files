package ru.ezhov.duplicate.files.stamp.generator.model.event;

public class StampGeneratorDomainPublisher {
    private static StampGeneratorDomainPublisher stampGeneratorDomainPublisher = new StampGeneratorDomainPublisher();


    private StampGeneratorDomainPublisher() {
    }

    public static StampGeneratorDomainPublisher instance() {
        return stampGeneratorDomainPublisher;
    }

    public void publish(Class<? extends StampGeneratorDomainEvent> event) {

    }
}
