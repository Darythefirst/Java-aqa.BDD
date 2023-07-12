package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {
    DashboardPage dashboardPage;

    @BeforeEach
    void setup(){
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldTransferMoneyFromFirstToSecond() {
        var firstCardinfo = DataHelper.getFirstCardInfo();
        var secondCardInfo = DataHelper.getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardinfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        int amount = 250;
        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount),firstCardinfo);
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardinfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedFirstCardBalance,actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance,actualSecondCardBalance);
    }
    @Test
    void shouldTransferMoneyFromSecondToFirst() {
        var firstCardinfo = DataHelper.getFirstCardInfo();
        var secondCardInfo = DataHelper.getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardinfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        int amount = 450;
        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;
        var transferPage = dashboardPage.selectCardToTransfer(firstCardinfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount),secondCardInfo);
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardinfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedFirstCardBalance,actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance,actualSecondCardBalance);
    }
    @Test
    void shouldNotTransferMoneyFromSecondToFirst() {
        var firstCardinfo = DataHelper.getFirstCardInfo();
        var secondCardInfo = DataHelper.getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardinfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        int amount = 11_000;
        var transferPage = dashboardPage.selectCardToTransfer(firstCardinfo);
        transferPage.makeTransfer(String.valueOf(amount),secondCardInfo);
        transferPage.findErrorNotification("Ошибка!Сумма перевода не должна превышать остаток по карте!");
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardinfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalance,actualFirstCardBalance);
        assertEquals(secondCardBalance,actualSecondCardBalance);
    }
}

