import { FileQuestionIcon } from "lucide-react"
import { Link, isRouteErrorResponse, useRouteError } from "react-router"

import { Button } from "@/components/ui/button"
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "@/components/ui/empty"

export function NotFoundPage() {
  return (
    <Message title="Страница не найдена" description="Такого адреса нет. Перейдите к списку фильмов." />
  )
}

export function RouteErrorPage() {
  const error = useRouteError()
  const description = isRouteErrorResponse(error) ? `${error.status} ${error.statusText}` : String(error?.message ?? error)
  return <Message title="Что-то пошло не так" description={description} />
}

function Message({ title, description }) {
  return (
    <Empty className="m-auto">
      <EmptyHeader>
        <EmptyMedia variant="icon"><FileQuestionIcon /></EmptyMedia>
        <EmptyTitle>{title}</EmptyTitle>
        <EmptyDescription>{description}</EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <Button asChild size="sm"><Link to="/movies">К фильмам</Link></Button>
      </EmptyContent>
    </Empty>
  )
}
