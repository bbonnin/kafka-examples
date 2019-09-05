package io.onurb.examples.avro;

import io.onurb.examples.avro.table2.TableRecord;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

public class AvroTolerantReader {

    public static void main(String[] args) throws IOException {

        final io.onurb.examples.avro.table1.TableRecord rec = new io.onurb.examples.avro.table1.TableRecord();
        rec.setTable("table1");

        //
        // Création d'un fichier avec le schema "table1"
        //
        final DatumWriter<io.onurb.examples.avro.table1.TableRecord> datumWriter =
                new SpecificDatumWriter<>(io.onurb.examples.avro.table1.TableRecord.class);
        final DataFileWriter<io.onurb.examples.avro.table1.TableRecord> dataFileWriter =
                new DataFileWriter<>(datumWriter);

        dataFileWriter.create(rec.getSchema(), new File("table.avro"));
        dataFileWriter.append(rec);
        dataFileWriter.close();

        //
        // Lecture en spécifiant le schema "table2"
        //
        System.out.println("*** LECTURE AVEC RECORD DU SCHEMA TABLE 2 ***");
        final DatumReader<TableRecord> datumReader =
                new SpecificDatumReader<>(TableRecord.class);
        final DataFileReader<TableRecord> dataFileReader =
                new DataFileReader<>(new File("table.avro"), datumReader);
        TableRecord record = null;

        while (dataFileReader.hasNext()) {
            record = dataFileReader.next(record);
            System.out.println("Schema: " + record.getSchema());
            System.out.println("Valeur du champ 'table': " + record.getTable());
        }

        //
        // Lecture en utilisant un record générique
        //
        System.out.println("\n*** LECTURE AVEC RECORD GENERIQUE ***");
        final GenericDatumReader genDatumReader = new GenericDatumReader();
        final DataFileReader genDataFileReader = new DataFileReader<>(new File("table.avro"), genDatumReader);
        GenericData.Record genRecord = null;

        while (genDataFileReader.hasNext()) {
            genRecord = (GenericData.Record) genDataFileReader.next(genRecord);
            System.out.println("Schema: " + genRecord.getSchema());
            System.out.println("Valeur du champ 'table': " + genRecord.get("table"));

        }
    }
}
