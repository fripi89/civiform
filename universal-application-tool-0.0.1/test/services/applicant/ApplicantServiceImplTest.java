package services.applicant;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Locale;
import models.Applicant;
import org.junit.Before;
import org.junit.Test;
import repository.ApplicantRepository;
import repository.WithPostgresContainer;
import services.ErrorAnd;
import services.program.ProgramDefinition;
import services.program.ProgramQuestionDefinition;
import services.program.ProgramService;
import services.program.ProgramServiceImpl;
import services.question.NameQuestionDefinition;
import services.question.QuestionDefinition;
import services.question.QuestionService;

public class ApplicantServiceImplTest extends WithPostgresContainer {

  private ApplicantServiceImpl subject;
  private ProgramService programService;
  private QuestionService questionService;
  private QuestionDefinition questionDefinition;
  private ProgramDefinition programDefinition;
  private ApplicantRepository applicantRepository;

  @Before
  public void setUp() throws Exception {
    subject = instanceOf(ApplicantServiceImpl.class);
    programService = instanceOf(ProgramServiceImpl.class);
    questionService = instanceOf(QuestionService.class);
    applicantRepository = instanceOf(ApplicantRepository.class);
    createQuestions();
    createProgram();
  }

  @Test
  public void update_emptySetOfUpdates_isNotAnErrorAndDoesNotChangeApplicant() {
    Applicant applicant = subject.createApplicant(1L).toCompletableFuture().join();
    ApplicantData applicantDataBefore = applicant.getApplicantData();

    ErrorAnd<ReadOnlyApplicantProgramService, String> errorAnd =
        subject
            .stageAndUpdateIfValid(
                applicant.id, programDefinition.id(), 1L, ImmutableSet.<Update>builder().build())
            .toCompletableFuture()
            .join();

    ApplicantData applicantDataAfter =
        applicantRepository.lookupApplicantSync(applicant.id).get().getApplicantData();

    assertThat(applicantDataAfter).isEqualTo(applicantDataBefore);
    assertThat(errorAnd.getResult()).isInstanceOf(ReadOnlyApplicantProgramService.class);
    assertThat(errorAnd.isError()).isFalse();
  }

  @Test
  public void update_withUpdates_isOk() {
    Applicant applicant = subject.createApplicant(1L).toCompletableFuture().join();

    ImmutableSet<Update> updates =
        ImmutableSet.of(
            Update.create(Path.create("applicant.name.first"), "Alice"),
            Update.create(Path.create("applicant.name.last"), "Doe"));

    ErrorAnd<ReadOnlyApplicantProgramService, String> errorAnd =
        subject
            .stageAndUpdateIfValid(applicant.id, programDefinition.id(), 1L, updates)
            .toCompletableFuture()
            .join();

    assertThat(errorAnd.getResult()).isInstanceOf(ReadOnlyApplicantProgramService.class);
    assertThat(errorAnd.isError()).isFalse();

    ApplicantData applicantDataAfter =
        applicantRepository.lookupApplicantSync(applicant.id).get().getApplicantData();

    assertThat(applicantDataAfter.asJsonString()).contains("Alice", "Doe");
  }

  @Test
  public void createApplicant_createsANewApplicant() {
    Applicant applicant = subject.createApplicant(1l).toCompletableFuture().join();

    assertThat(applicant.id).isNotNull();
  }

  @Test
  public void getReadOnlyApplicantService_getsReadOnlyApplicantServiceForTheApplicantAndProgram() {
    Applicant applicant = subject.createApplicant(1l).toCompletableFuture().join();

    ReadOnlyApplicantProgramService roApplicantProgramService =
        subject
            .getReadOnlyApplicantProgramService(applicant.id, programDefinition.id())
            .toCompletableFuture()
            .join();

    assertThat(roApplicantProgramService).isInstanceOf(ReadOnlyApplicantProgramService.class);
  }

  private void createQuestions() {
    questionDefinition =
        questionService
            .create(
                new NameQuestionDefinition(
                    1L,
                    "my name",
                    "applicant.name",
                    "description",
                    ImmutableMap.of(Locale.ENGLISH, "question?"),
                    ImmutableMap.of(Locale.ENGLISH, "help text")))
            .getResult();
  }

  private void createProgram() throws Exception {
    programDefinition = programService.createProgramDefinition("test program", "desc");
    programDefinition =
        programService.addBlockToProgram(
            programDefinition.id(), "test block", "test block description");
    programDefinition =
        programService.setBlockQuestions(
            programDefinition.id(),
            programDefinition.blockDefinitions().get(0).id(),
            ImmutableList.of(ProgramQuestionDefinition.create(questionDefinition)));
  }
}
