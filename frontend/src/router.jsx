import { createBrowserRouter, Navigate } from "react-router"

import { AppLayout } from "@/components/app-layout"
import { MoviesPage } from "@/features/movies/movies-page"
import { NotFoundPage, RouteErrorPage } from "@/features/not-found-page"
import { OscarsPage } from "@/features/oscars/oscars-page"

export const router = createBrowserRouter([
  {
    element: <AppLayout />,
    errorElement: <RouteErrorPage />,
    children: [
      { index: true, element: <Navigate to="/movies" replace /> },
      {
        path: "movies",
        element: <MoviesPage />,
        handle: { title: "Фильмы" },
        // карточка фильма открывается поверх списка: /movies/42 — ссылкой можно поделиться
        children: [{ path: ":movieId" }],
      },
      { path: "oscars", element: <OscarsPage />, handle: { title: "Оскары" } },
      { path: "*", element: <NotFoundPage />, handle: { title: "Страница не найдена" } },
    ],
  },
])
