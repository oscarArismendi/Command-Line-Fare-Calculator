package org.example.infrastructure.repositories

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.application.ports.out.FareTariffPort
import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest
import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal
import kotlin.plus

class ExcelFareTariffRepository: FareTariffPort {
    override fun findFare(fareRequest: FareRequest): FareCalculationResult {
        val file = File("src/main/kotlin/config/tariff_V1.xlsx")
        val inputStream = FileInputStream(file)
        val workbook: Workbook = XSSFWorkbook(inputStream)

        try {
            val productsSheet: Sheet = workbook.getSheetAt(0)
            val journeysSheet: Sheet = workbook.getSheetAt(1)
            val fareSheet: Sheet = workbook.getSheetAt(2)

            val selectionKey = findSelectionKey(journeysSheet, fareRequest.origin, fareRequest.destination)
            if(selectionKey == null){
                throw IllegalArgumentException("Journey not found for origin: ${fareRequest.origin}, destination: ${fareRequest.destination}")
            }

            val productInfo = findProductInfo(productsSheet, fareRequest.riderType)
            if(productInfo == null){
                throw IllegalArgumentException("Product not found for rider type: ${fareRequest.riderType}")
            }

            val totalFare = findTotalFare(fareSheet, selectionKey, productInfo.reference)
            if(totalFare == null){
                throw IllegalArgumentException("Total fare not found for selection key: $selectionKey, product reference: ${productInfo.reference}")
            }
            return FareCalculationResult(baseFare = totalFare + productInfo.discount, discount = productInfo.discount)

        }catch (e: Exception){
            throw e
        }



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
        val headerRow = productsSheet.getRow(1) // Tab names are in row 1

        var riderTypeColumn = -1
        var discountColumn = -1

        for (cellIndex in 0 until headerRow.lastCellNum) {
            val cellValue = getCellValue(headerRow.getCell(cellIndex)).lowercase()
            when {
                cellValue.contains("rider type") -> riderTypeColumn = cellIndex
                cellValue.contains("discount") -> discountColumn = cellIndex
            }
        }

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
                    discount = BigDecimal(getCellValue(discountCell))
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

    private fun getCellValue(cell: Cell?): String {
        return when (cell?.cellType) {
            CellType.STRING -> cell.stringCellValue.trim()
            CellType.NUMERIC -> cell.numericCellValue.toString()
            else -> ""
        }
    }

    private data class ProductInfo(
        val reference: String,
        val discount: BigDecimal
    )
}