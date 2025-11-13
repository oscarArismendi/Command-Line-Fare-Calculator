package org.fare.calculator.infrastructure.repositories

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.fare.calculator.application.ports.out.FareTariffPort
import org.fare.calculator.domain.dtos.FareCalculationResult
import org.fare.calculator.domain.models.Fare
import org.fare.calculator.domain.models.RiderType
import org.fare.calculator.domain.models.Station
import org.fare.calculator.domain.models.Trip
import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal

// Question: A constant should be in other class? or since this is only used within this class, it should remain here?
private const val NULL_COLUMN = -1

private const val FIRST_DATA_ROW = 2

private const val JOURNEY_SHEET_NUMBER = 1
private const val FARE_SHEET_NUMBER = 2
private const val PRODUCT_SHEET_NUMBER = 0

class ExcelFareTariffRepository : FareTariffPort {

    private val logger = KotlinLogging.logger {}
    // TODO: Excel repository should be able to find the latest version
    private val file = File("src/main/kotlin/config/tariff_V1.xlsx")

    fun <T> withWorkbook(operation: (Workbook) -> T): T = FileInputStream(file).use{ inputStream ->
        XSSFWorkbook(inputStream).use{ workbook ->
            operation(workbook)
        }
    }

    // TODO: Implement cache for better performance(journeysSheet, fareSheet and productsSheet)

    override fun findFare(trip: Trip): FareCalculationResult = withWorkbook { workbook ->
        try {
            val journeysSheet = workbook.getSheetAt(JOURNEY_SHEET_NUMBER)
            val productsSheet = workbook.getSheetAt(PRODUCT_SHEET_NUMBER)
            val fareSheet = workbook.getSheetAt(FARE_SHEET_NUMBER)

            val selectionKey = findSelectionKey(journeysSheet, trip.origin.name, trip.destination.name)
            if (selectionKey == null) {
                logger.error { "Journey not found in the tariff -> ${trip.origin.name} to ${trip.destination.name}" }
                throw IllegalArgumentException(
                    "Journey not found for origin: ${trip.origin.name}, destination: ${trip.destination.name}",
                )
            }

            val productInfo = findProductInfo(productsSheet, trip.riderType.name)
            if (productInfo == null) {
                logger.error { "Product not found in the tariff -> ${trip.riderType.name}" }
                throw IllegalArgumentException("Product not found for rider type: ${trip.riderType.name}")
            }

            val totalFare = findTotalFare(fareSheet, selectionKey, productInfo.reference)
            if (totalFare == null) {
                logger.error { "Total fare not found for selection key: -> $selectionKey" }
                throw IllegalArgumentException(
                    "Total fare not found for selection key: $selectionKey, product reference: ${productInfo.reference}",
                )
            }
            // TODO: Use the real currency
            val baseFare = Fare(totalFare, "USD")
            val discount = Fare(productInfo.discount, "USD")
            FareCalculationResult(baseFare = baseFare, discount = discount)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    override fun getAllStations(): Set<Station> = withWorkbook { workbook ->
        val journeysSheet = workbook.getSheetAt(JOURNEY_SHEET_NUMBER)
        val response = mutableSetOf<Station>()
        val destinationRow = journeysSheet.getRow(1)
        for (cellIndex in 1 until destinationRow.lastCellNum) {
            val cell = destinationRow.getCell(cellIndex)
            response.add(Station(id = 1, name = getCellValue(cell)))
            // TODO: Need a database to save station IDs
        }
        response
    }

    override fun getAllRiderTypes(): Set<RiderType> = withWorkbook { workbook ->
        val productsSheet = workbook.getSheetAt(PRODUCT_SHEET_NUMBER)

        val response = mutableSetOf<RiderType>()
        val riderTypeColumn = getProductSheetColumn(productsSheet,"rider type")

        if (riderTypeColumn == NULL_COLUMN) return@withWorkbook response

        // Find matching rider type row (starting from row 2)
        for (rowIndex in FIRST_DATA_ROW..productsSheet.lastRowNum) {
            val row = productsSheet.getRow(rowIndex)
            val riderTypeCell = row.getCell(riderTypeColumn)
            val cellValue = getCellValue(riderTypeCell).uppercase()
            val riderType = RiderType.entries.find { it.value.uppercase() == cellValue }
            if (riderType == null) {
                logger.error { "Unsupported rider type on the tariff: $cellValue" }
                continue
            }
            response.add(riderType)
        }
        response
    }

    private fun findSelectionKey(journeysSheet: Sheet, origin: String, destination: String): String? {

        // Get destination row (row 1)
        val destinationRow = journeysSheet.getRow(1)

        // Find destination column
        var destinationColumn = NULL_COLUMN
        for (cellIndex in 1 until destinationRow.lastCellNum) {
            val cell = destinationRow.getCell(cellIndex)
            if (getCellValue(cell) == destination) {
                destinationColumn = cellIndex
                break
            }
        }
        if (destinationColumn == NULL_COLUMN) return null

        // Find origin row (starting from row 2)
        for (rowIndex in FIRST_DATA_ROW..journeysSheet.lastRowNum) {
            val row = journeysSheet.getRow(rowIndex)
            val originCell = row.getCell(0)
            if (getCellValue(originCell) == origin) {
                // Found the intersection - get the selection key
                val selectionKeyCell = row.getCell(destinationColumn)
                return getCellValue(selectionKeyCell)
            }
        }

        return null
    }

    private fun findProductInfo(productsSheet: Sheet, riderType: String): ProductInfo? {
        val riderTypeColumn = getProductSheetColumn(productsSheet,"rider type")
        val discountColumn = getProductSheetColumn(productsSheet,"discount")

        if (riderTypeColumn == NULL_COLUMN || discountColumn == NULL_COLUMN) return null

        // Find matching rider type row (starting from row 2)
        for (rowIndex in FIRST_DATA_ROW..productsSheet.lastRowNum) {
            val row = productsSheet.getRow(rowIndex)
            val riderTypeCell = row.getCell(riderTypeColumn)

            if (getCellValue(riderTypeCell).uppercase() == riderType.uppercase()) {
                val referenceCell = row.getCell(0)
                val discountCell = row.getCell(discountColumn)

                return ProductInfo(
                    reference = getCellValue(referenceCell),
                    discount = BigDecimal(getCellValue(discountCell)),
                )
            }
        }

        return null
    }

    private fun findTotalFare(fareSheet: Sheet, selectionKey: String, productReference: String): BigDecimal? {
        val headerRow = fareSheet.getRow(1) // The Second row contains product references

        // Find product reference column
        var productColumn = NULL_COLUMN
        for (cellIndex in 1 until headerRow.lastCellNum) {
            val cell = headerRow.getCell(cellIndex)
            if (getCellValue(cell) == productReference) {
                productColumn = cellIndex
                break
            }
        }

        if (productColumn == NULL_COLUMN) return null

        // Find selection key row (starting from row 2)
        for (rowIndex in FIRST_DATA_ROW..fareSheet.lastRowNum) {
            val row = fareSheet.getRow(rowIndex)
            val selectionKeyCell = row.getCell(0)

            if (getCellValue(selectionKeyCell) == selectionKey) {
                // Found the intersection - get the fare
                val fareCell = row.getCell(productColumn)
                val fareValue = getCellValue(fareCell)
                return BigDecimal(fareValue)
            }
        }

        return null
    }

    private fun getProductSheetColumn(productsSheet: Sheet, columnName: String): Int {
            val headerRow = productsSheet.getRow(1) // Tab names are in row 1

            var columnIndex = NULL_COLUMN
            for (cellIndex in 0 until headerRow.lastCellNum) {
                val cellValue = getCellValue(headerRow.getCell(cellIndex)).lowercase()
                when {
                    cellValue.contains(columnName) -> columnIndex = cellIndex
                }
            }
            return columnIndex
    }

    private fun getCellValue(cell: Cell?): String {
        return when (cell?.cellType) {
            CellType.STRING -> cell.stringCellValue.trim()
            CellType.NUMERIC -> cell.numericCellValue.toString()
            else -> ""
        }
    }

    private data class ProductInfo(
        val reference: String,
        val discount: BigDecimal,
    )
}
