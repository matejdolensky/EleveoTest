package pageobjects;

import com.codeborne.selenide.*;
import utils.Utils;


import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class MainPagePO {

    public ResultsPO searchForConnections(String  nearestMondayDay, String nearestMondayMonth, String departureStation, String arrivalStation) {

        if (Utils.waitForVisibilityOfElement($(byText("Přejít na nový web")))){
            open("https://novy.regiojet.cz/sk?optimize=1");
        }

        if (Utils.waitForVisibilityOfElement($(byText("PRIJAŤ VŠETKY")))) {
            $(byText("PRIJAŤ VŠETKY")).click();
        }

        $(".react-select__input-container.css-lwhfup .react-select__input", 0).setValue(departureStation).pressEnter();

        $(".react-select__input-container.css-lwhfup .react-select__input", 1).setValue(arrivalStation).pressEnter();

        $(byText("Odchod")).sibling(0).click();


        $(byText(nearestMondayMonth)).parent().sibling(0).$(byText(nearestMondayDay)).click();

        $(byAttribute("data-id", "search-btn")).click();

        return page(ResultsPO.class);
    }
}
