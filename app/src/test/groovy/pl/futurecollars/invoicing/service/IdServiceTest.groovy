package pl.futurecollars.invoicing.service

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class IdServiceTest extends Specification{

    private Path nextIdDbPath = File.createTempFile('nextId', '.txt').toPath()

    def "next id starts from 1 if file was empty"() {
        given:
        IdService idService = new IdService(nextIdDbPath, new FileService())

        expect:
        ['1'] == Files.readAllLines(nextIdDbPath)

        and:
        1 == idService.getNextId()
        ['2'] == Files.readAllLines(nextIdDbPath)

        and:
        2 == idService.getNextId()
        ['3'] == Files.readAllLines(nextIdDbPath)

        and:
        3 == idService.getNextId()
        ['4'] == Files.readAllLines(nextIdDbPath)
    }

    def "next id starts from last number if file was not empty"() {
        given:
        Files.writeString(nextIdDbPath, "11")
        IdService idService = new IdService(nextIdDbPath, new FileService())

        expect:
        ['11'] == Files.readAllLines(nextIdDbPath)

        and:
        11 == idService.getNextId()
        ['12'] == Files.readAllLines(nextIdDbPath)

        and:
        12 == idService.getNextId()
        ['13'] == Files.readAllLines(nextIdDbPath)

        and:
        13 == idService.getNextId()
        ['14'] == Files.readAllLines(nextIdDbPath)
    }

}
