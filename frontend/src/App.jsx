import { RouterProvider } from 'react-router/dom'

import { Toaster } from '@/components/ui/sonner'
import { TooltipProvider } from '@/components/ui/tooltip'
import { router } from '@/router'

function App() {
  return (
    <TooltipProvider>
      <RouterProvider router={router} />
      {/* тема интерфейса всегда светлая: без theme тосты брали бы тёмную тему системы */}
      <Toaster richColors theme="light" />
    </TooltipProvider>
  )
}

export default App
