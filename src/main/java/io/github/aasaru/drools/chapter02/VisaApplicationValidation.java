/*
 * Copyright 2017 Juhan Aasaru.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.aasaru.drools.chapter02;

import io.github.aasaru.drools.Common;
import io.github.aasaru.drools.domain.Passport;
import io.github.aasaru.drools.domain.Visa;
import io.github.aasaru.drools.domain.VisaApplication;
import io.github.aasaru.drools.repository.ApplicationRepository;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;

import java.util.Collection;
import java.util.List;

public class VisaApplicationValidation {
  public static void main(final String[] args) {
    execute(Common.promptForStep(2, args, 1, 4));
  }


  static void execute(int step) {
    System.out.println("Running step " + step);
    KieSession ksession = KieServices.Factory.get().getKieClasspathContainer().newKieSession("VisaApplicationStep" + step);

    List<Passport> passports = ApplicationRepository.getPassports();
    passports.forEach(ksession::insert);

    List<VisaApplication> visaApplications = ApplicationRepository.getVisaApplications();
    visaApplications.forEach(ksession::insert);

    if (step == 3) {
      Agenda agenda = ksession.getAgenda();
      agenda.getAgendaGroup("valid-application").setFocus();
      agenda.getAgendaGroup("invalid-application").setFocus();
      agenda.getAgendaGroup("valid-passport").setFocus();
      agenda.getAgendaGroup("invalid-passport").setFocus();
    }

    if (step == 4) {
      Agenda agenda = ksession.getAgenda();
      agenda.getAgendaGroup("issue-visa").setFocus();
      agenda.getAgendaGroup("valid-application").setFocus();
      agenda.getAgendaGroup("invalid-application").setFocus();
      agenda.getAgendaGroup("valid-passport").setFocus();
      agenda.getAgendaGroup("invalid-passport").setFocus();
    }

    ksession.fireAllRules();
    ksession.dispose();


    System.out.println("==== APPLICATIONS STATE AFTER DROOLS SESSION === ");
    visaApplications.forEach(visaApplication -> System.out.println(visaApplication + " verdict: " + visaApplication.getValidation()));

    if (step > 2) {
      Collection<?> ksessionObjects = ksession.getObjects();
      System.out.println("== All objects from session == ");
      ksessionObjects.forEach(System.out::println);


      Collection<?> visaObjects = ksession.getObjects(o -> o.getClass() == Visa.class);
      System.out.println("== Visas from session == ");
      visaObjects.forEach(System.out::println);
    }


  }

}