import java.util.Arrays;
import java.util.function.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;

public class CarThing {
  static class Car {
    int id;
    String make;
    String model;
    int manufacturingYear;
    String color;
    int price;
    int registrationNumber;

    public Car(
        int id,
        String make,
        String model,
        int manufacturingYear,
        String color,
        int price,
        int registrationNumber) {
      this.id = id;
      this.make = make;
      this.model = model;
      this.manufacturingYear = manufacturingYear;
      this.color = color;
      this.price = price;
      this.registrationNumber = registrationNumber;
    }

    public String toString() {
      return String.format(
          "Car(id=%d, make=%s, model=%s, manufacturingYear=%d, color=%s, price=%d, registrationNumber=%d)",
          id, make, model, manufacturingYear, color, price, registrationNumber);
    }
  }

  static void writeFilteredCars(Car[] cars, Path file, Predicate<Car> p) {
    try {
      try (var bw = Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
        Arrays.stream(cars).filter(p).forEach(c -> {
          try {
            bw.write(c.toString());
            bw.newLine();
            // NOTE: checked exceptions don't bubble up in lambdas
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      }
    } catch (Exception e) {
      System.out.printf("Failed writing to %s: %s\n", file, e);
    }
  }

  static void writeByBrand(Car[] cars, Path file, String brand) {
    writeFilteredCars(cars, file, c -> brand == c.make);
  }

  static void writeByYear(Car[] cars, Path file, int currentYear, int n) {
    writeFilteredCars(cars, file, c -> {
      assert currentYear > c.manufacturingYear;
      return currentYear - c.manufacturingYear > n;
    });
  }

  static void writeC(Car[] cars, Path file, int manufacturingYear, int price) {
    writeFilteredCars(cars, file, c -> c.manufacturingYear == manufacturingYear && c.price > price);
  }

  public static void main(String[] args) {
    Car[] cars = {
        new Car(1, "Honda", "Civic", 2012, "silver", 1000, 32424),
        new Car(2, "Honda", "Jazz", 2012, "orange", 3000, 32256),
        new Car(3, "Volkswagen", "Golf", 2021, "beige", 3000, 4356),
        new Car(4, "Volkswagen", "Polo", 2001, "beige", 3000, 5),
    };
    var outputDir = Path.of("output");
    try {
      // NOTE: createDirectory fails on existing dir
      Files.createDirectories(outputDir);
    } catch (IOException e) {
      System.out.printf("Failed creating directory `%s`: %s\n", outputDir, e);
    }

    // a)
    writeByBrand(cars, outputDir.resolve("a.txt"), "Honda");

    // b)
    writeByYear(cars, outputDir.resolve("b.txt"), 2024, 4);

    // c)
    writeC(cars, outputDir.resolve("c.txt"), 2012, 1500);
  }
}
