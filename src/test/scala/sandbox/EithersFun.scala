package sandbox

import scala.concurrent.Future
import scalaz._
import Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global

trait ReportService {
  def reportFor(group: Group): Future[\/[String, Report]]
}
class ReportServiceImpl extends ReportService {
  def reportFor(group: Group): Future[\/[String, Report]] =
    Future {
      \/-(Report(s"Example report for group ${group.name}"))
    }
}

class ReportSandbox(service: ReportService) {

  def reportsForGroups(groups: List[Group]): Future[\/[String, ResultsContainer]] = {
    val eventualResults = groups.map(resultForGroup)
    val seqResults = Future.sequence(eventualResults)
    seqResults.map(mergeResults)
  }

  private def mergeResults(results: List[GroupToResult]) = {
    results.partition(_.result.isRight) match {
      case (s, Nil) => \/-(combineSuccesses(s))
      case (_, f) => -\/(findFailures(f).getOrElse("Service returned failure, but no failure messages found."))
    }
  }

  private def resultForGroup(group: Group) = service.reportFor(group).map(result => GroupToResult(group, result))

  private def combineSuccesses(results: List[GroupToResult]): ResultsContainer = {
    val reportsForGroups = for {
      GroupToResult(group, result) <- results.view
      report <- result.toOption
    } yield group -> report

    ResultsContainer(reportsForGroups.toMap)
  }

  private def findFailures(results: List[GroupToResult]): Option[String] = {
    val failedResults = for {
      GroupToResult(group, result) <- results.view
      errorMsg <- result.swap.toOption
    } yield s"Error occurred for group $group : $errorMsg"

    failedResults.headOption.fold(none[String])(_ => failedResults.mkString(", ").some)
  }
}

case class Report(name: String)
case class Group(name: String)
case class GroupToResult(group: Group, result: \/[String, Report])
case class ResultsContainer(results: Map[Group, Report])
