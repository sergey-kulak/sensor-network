package com.ita.sensornetwork.sensor

import java.time.{LocalDate, LocalDateTime, LocalTime}

import com.ita.sensornetwork.TestEntityKit
import com.ita.sensornetwork.common._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpecLike}

class SensorDaoImplSpec extends TestEntityKit with WordSpecLike with Matchers with TableDrivenPropertyChecks {

  "SensorDaoImpl" should {

    "register a new sensor" in withRollback {
      val expectedSerialNumber = "123"
      val expectedRegDate = LocalDateTime.now()
      val expectedParams: Set[MeasurableParameter] = Set(MeasurableParameter.Location, MeasurableParameter.NoiseLevel)

      val registerDto = RegisterSensor(expectedSerialNumber, expectedRegDate, expectedParams)

      sensorDao.registerAction(registerDto)
        .map { sensor =>
          assert(sensor.id != 0L)
          assert(sensor.serialNumber === expectedSerialNumber)
          assert(sensor.registrationDate === expectedRegDate)
          assert(sensor.measurableParameters === expectedParams)
        }
    }

    "find by id" in withRollback {
      for {
        sensor <- registerSensor()
        foundOption <- sensorDao.findByIdAction(sensor.id)
      } yield {
        assert(foundOption.isDefined)
        foundOption.map { fs =>
          assert(fs === sensor)
          assert(fs.measurableParameters === sensor.measurableParameters)
        }
      }
    }

    "return none if there is no result during find by id" in withRollback {
      sensorDao.findByIdAction(-1).map { fs =>
        assert(fs.isEmpty)
      }
    }

    "return all sensors" in withRollback {
      for {
        sensor <- registerSensor()
        sensors <- sensorDao.findAllAction()
      } yield {
        assert(sensors.nonEmpty)
        assert(sensors.contains(sensor))
        assert(sensor.measurableParameters.nonEmpty)
      }
    }

    "save sensor data" in withRollback {
      registerSensor().flatMap { sensor =>
        val expectedValue: Double = 10.5
        val expectedParam = sensor.measurableParameters.head
        val sensorData = CreateSensorData(expectedParam, expectedValue)
        sensorDao.saveSensorDataAction(sensor.id, sensorData).map { sensorData =>
          assert(sensorData.id != 0L)
          assert(sensorData.sensorId === sensor.id)
          assert(sensorData.measurableParameter === expectedParam)
          sensorData.value should equal(expectedValue)
        }
      }
    }

    val sensorDataFilters = Seq(
      (_: Sensor, request: PageRequest) =>
        SensorDataFilter(request.copy(sort = Sort(PageRequest.IdField, SortDirection.Desc))),

      (s: Sensor, request: PageRequest) =>
        SensorDataFilter(request.copy(sort = Sort(SensorData.Time, SortDirection.Desc)), None, Some(s.serialNumber)),

      (s: Sensor, request: PageRequest) =>
        SensorDataFilter(request.copy(sort = Sort(Sensor.SerialNumber, SortDirection.Desc)),
          Some(s.id), Some(s.serialNumber))
    )

    sensorDataFilters.zipWithIndex.foreach { case (filterBuilder, i) =>
      s"find sensor data with paging with filter #${i + 1}" in withRollback {
        val pageRequest = PageRequest(0, 10)
        for {
          sensor <- registerSensor()
          otherSensor <- registerSensor()
          sensorData <- addSensorData(sensor)
          _ <- addSensorData(otherSensor)
          page <- sensorDao.findSensorDataAction(filterBuilder(sensor, pageRequest))
        } yield assertFoundSensorData(sensor, sensorData, page, filterBuilder(sensor, pageRequest))
      }
    }

    def assertFoundSensorData(sensor: Sensor, sensorData: SensorData,
                              page: Page[FullSensorData], filter: SensorDataFilter) = {
      val pageRequest = filter.pageRequest
      if (filter.sensorId.isEmpty && filter.sensorSerialNumber.isEmpty) {
        assert(page.totalItems > 0)
      } else {
        assert(page.totalItems === 1)
      }
      val expectedPageCount = (page.totalItems.toFloat / pageRequest.length).ceil.toInt
      assert(page.totalPages === expectedPageCount)
      assert(pageRequest.pageNumber === page.pageNumber)

      val content = page.content
      val foundSensorData = content.find(_.id == sensorData.id)
      assert(foundSensorData.isDefined)
      assert(foundSensorData.get.sensor.id === sensor.id)
      assert(foundSensorData.get.sensor.serialNumber === sensor.serialNumber)
    }

    "return empty result during finding sensor data with paging" in withRollback {
      val pageRequest = PageRequest(0, 10)
      for {
        sensor <- registerSensor()
        page <- sensorDao.findSensorDataAction(SensorDataFilter(pageRequest, sensorId = Some(sensor.id)))
      } yield {
        assert(page.totalItems === 0)
        assert(page.totalPages === 0)
        assert(page.content.isEmpty)
      }
    }

    "findSensorMaxStatistics should find sensor data with max" in withRollback {
      val filter = SensorMaxStatisticsFilter(Some(LocalDate.now().atStartOfDay()),
        Some(LocalDate.now().atTime(LocalTime.MAX)))
      for {
        noDataSensor <- registerSensor()

        wrongDateSensor <- registerSensor()
        _ <- addSensorData(wrongDateSensor, time = LocalDateTime.now().minusMonths(1))
        _ <- addSensorData(wrongDateSensor, time = LocalDateTime.now().plusMonths(1))

        sensor <- registerSensor()
        sensorData <- addSensorData(sensor)
        biggerSensorData <- addSensorData(sensor, sensorData.value + 1)

        stat <- sensorDao.findSensorMaxStatisticsAction(filter)
      } yield {
        assert(stat.nonEmpty)
        val statMap = stat.map(i => (i._1, i._2)).toMap
        assert(statMap.get(wrongDateSensor).isDefined)
        assert(statMap.get(wrongDateSensor).flatten.isEmpty)

        assert(statMap.get(noDataSensor).isDefined)
        assert(statMap.get(noDataSensor).flatten.isEmpty)

        val foundMaxData = statMap.get(sensor)
        assert(foundMaxData.isDefined)
        foundMaxData.flatten.map(md => {
          assert(md.id === biggerSensorData.id)
        })
      }
    }
  }
}
