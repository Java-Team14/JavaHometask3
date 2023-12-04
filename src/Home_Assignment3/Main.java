package Home_Assignment3;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ChargingStation {
    private final int stationId;
    private final Queue<Car> queue;

    public ChargingStation(int stationId) {
        this.stationId = stationId;
        this.queue = new LinkedList<>();
    }
    
    public synchronized void entryQueueCar(Car car) {
        long startTime = System.currentTimeMillis();
        queue.add(car);
        long endTime = System.currentTimeMillis();
        long waitingTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);

        // Check if waiting time exceeds 15 minutes, and if so, remove the car from the queue
        if (waitingTimeInSeconds > 15 * 60) {
            System.out.println("Car " + car.getCarId() + " waited more than 15 minutes and left the queue at Station " + stationId);
            queue.remove(car);
        }
    }
    
    public void chargeCars() {
        while (true) {
            Car car;
            synchronized (this) {
                car = queue.poll();
            }
            if (car != null) {
                long chargingStartTime = System.currentTimeMillis();
                chargeCar(car);
                long chargingEndTime = System.currentTimeMillis();
                long chargingTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(chargingEndTime - chargingStartTime);
                System.out.println("Car " + car.getCarId() + " took " + chargingTimeInSeconds + " seconds to charge at Station " + stationId);
            } else {
                break;
            }
        }
    }
    
    private void chargeCar(Car car) {
        System.out.println("Car " + car.getCarId() + " is charging at Station " + stationId);
        try {
            TimeUnit.SECONDS.sleep(new Random().nextInt(5) + 1); // Simulating charging time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Car " + car.getCarId() + " finished charging at Station " + stationId);
    }
    
    public void chargeReservedBatteries() {
        System.out.println("Charging reserved batteries at Station " + stationId);

        // Simulate charging process for reserved batteries using solar, wind, and hydro as energy sources
        for (int i = 0; i < 5; i++) {
            chargeWithSolar();
            chargeWithWind();
            chargeWithHydro();
        }
    }

    private void chargeWithSolar() {
        System.out.println("Charging reserved batteries with Solar at Station " + stationId);
        simulateChargingTime();
    }

    private void chargeWithWind() {
        System.out.println("Charging reserved batteries with Wind at Station " + stationId);
        simulateChargingTime();
    }

    private void chargeWithHydro() {
        System.out.println("Charging reserved batteries with Hydro at Station " + stationId);
        simulateChargingTime();
    }

    private void simulateChargingTime() {
        // Simulate charging time
        try {
            TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 1); // Simulating charging time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Car {
    private final int carId;

    public Car(int carId) {
        this.carId = carId;
    }
    public int getCarId() {
        return carId;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter the resource (Available resources : Diesel, Petrol and CNG): ");
            String resource = scanner.nextLine();

            if (!resource.equalsIgnoreCase("diesel") && !resource.equalsIgnoreCase("petrol") && !resource.equalsIgnoreCase("cng")) {
                throw new InputMismatchException("Invalid input. Please enter Diesel or Petrol or CNG");
            }

            int numberOfStations = 3;
            int numberOfCars = 10;
            
            ChargingStation[] chargingStations = new ChargingStation[numberOfStations];
            for (int i = 0; i < numberOfStations; i++) {
                chargingStations[i] = new ChargingStation(i + 2); // Charging stations 2...N
            }

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfStations);

            for (int i = 1; i <= numberOfCars; i++) {
                Car car = new Car(i);

                int stationId = new Random().nextInt(numberOfStations) + 2; // Randomly choose a charging station 2...N
                chargingStations[stationId - 2].entryQueueCar(car);
            }

            for (ChargingStation station : chargingStations) {
                executorService.submit(station::chargeCars);
            }
            
            for (ChargingStation station : chargingStations) {
                executorService.submit(station::chargeReservedBatteries);
            }

            executorService.shutdown();
        } catch (InputMismatchException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}