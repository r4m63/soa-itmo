import { useEffect } from "react"
import { Outlet, useMatches } from "react-router"

import { AppSidebar } from "@/components/app-sidebar"
import { SiteHeader } from "@/components/site-header"
import { SidebarInset, SidebarProvider } from "@/components/ui/sidebar"

const APP_NAME = "Кинотека"

/** Каркас всех страниц: сайдбар, шапка с заголовком текущего маршрута (handle.title) и сама страница. */
export function AppLayout() {
  const title = useMatches().findLast((match) => match.handle?.title)?.handle.title

  useEffect(() => {
    document.title = title ? `${title} — ${APP_NAME}` : APP_NAME
  }, [title])

  return (
    <SidebarProvider
      style={{
        "--sidebar-width": "calc(var(--spacing) * 64)",
        "--header-height": "calc(var(--spacing) * 12)",
      }}
    >
      <AppSidebar variant="inset" />
      <SidebarInset>
        <SiteHeader title={title} />
        <main className="@container/main flex flex-1 flex-col">
          <Outlet />
        </main>
      </SidebarInset>
    </SidebarProvider>
  )
}
