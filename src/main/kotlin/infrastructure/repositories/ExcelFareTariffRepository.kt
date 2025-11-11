package org.example.infrastructure.repositories

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.application.ports.out.FareTariffPort
import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest
import org.example.domain.models.Fare
import org.example.domain.models.RiderType
import org.example.domain.models.Station
import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal

class ExcelFareTariffRepository : FareTariffPort {

    private val logger = KotlinLogging.logger {}

    private val file = File("src/main/kotlin/config/tariff_V1.xlsx")

    fun <T> withWorkbook(operation: (Workbook) -> T): T = FileInputStream(file).use{ inputStream ->
        XSSFWorkbook(inputStream).use{ workbook ->
            operation(workbook)
        }
    }

    // TODO: Implement cache for better performance(journeysSheet, fareSheet and productsSheet)

    override fun findFare(fareRequest: FareRequest): FareCalculationResult = withWorkbook { workbook ->
        try {
            val journeysSheet = workbook.getSheetAt(1)
            val productsSheet = workbook.getSheetAt(0)
            val fareSheet = workbook.getSheetAt(2)

            val selectionKey = findSelectionKey(journeysSheet, fareRequest.origin, fareRequest.destination)
            if (selectionKey == null) {
                logger.error { "Journey not found in the tariff -> ${fareRequest.origin} to ${fareRequest.destination}" }
                throw IllegalArgumentException(
                    "Journey not found for origin: ${fareRequest.origin}, destination: ${fareRequest.destination}",
                )
            }

            val productInfo = findProductInfo(productsSheet, fareRequest.riderType)
            if (productInfo == null) {
                logger.error { "Product not found in the tariff -> ${fareRequest.riderType}" }
                throw IllegalArgumentException("Product not found for rider type: ${fareRequest.riderType}")
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
        val journeysSheet = workbook.getSheetAt(1)
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
        val productsSheet = workbook.getSheetAt(0)

        val response = mutableSetOf<RiderType>()
        val riderTypeColumn = getProductSheetColumn(productsSheet,"rider type")

        if (riderTypeColumn == -1) return@withWorkbook response

        // Find matching rider type row (starting from row 2)
        for (rowIndex in 2..productsSheet.lastRowNum) {
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
        var destinationColumn = -1
        for (cellIndex in 1 until destinationRow.lastCellNum) {
            val cell = destinationRow.getCell(cellIndex)
            if (getCellValue(cell) == destination) {
                destinationColumn = cellIndex
                break
            }
        }
        // TODO: Remove magic numbers like the one below "-1" and just put an enum with that value and name it "NULL_COLUMN"
        if (destinationColumn == -1) return null

        // Find origin row (starting from row 2)
        for (rowIndex in 2..journeysSheet.lastRowNum) {
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

        if (riderTypeColumn == -1 || discountColumn == -1) return null

        // Find matching rider type row (starting from row 2)
        for (rowIndex in 2..productsSheet.lastRowNum) {
            val row = productsSheet.getRow(rowIndex)
            val riderTypeCell = row.getCell(riderTypeColumn)

            if (getCellValue(riderTypeCell) == riderType) {
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
        var productColumn = -1
        for (cellIndex in 1 until headerRow.lastCellNum) {
            val cell = headerRow.getCell(cellIndex)
            if (getCellValue(cell) == productReference) {
                productColumn = cellIndex
                break
            }
        }

        if (productColumn == -1) return null

        // Find selection key row (starting from row 2)
        for (rowIndex in 2..fareSheet.lastRowNum) {
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

            var columnIndex = -1
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
