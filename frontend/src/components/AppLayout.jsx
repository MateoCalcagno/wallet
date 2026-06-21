import { useRef, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import NavItem from './NavItem'
import NovaLogo from './NovaLogo'
import { useDashboard } from '../hooks/useDashboard'

function AppLayout({ children, onStartTour, refHelpBtn }) {
  const { user, handleLogout } = useDashboard()
  const [showProfile, setShowProfile] = useState(false)
  const profileRef = useRef(null)
  const navigate = useNavigate()
  const location = useLocation()

  return (
    <div className="flex min-h-screen">

      {/* Sidebar */}
      <div className="w-52 bg-slate-900 flex flex-col justify-between py-7 px-5 relative overflow-hidden shrink-0">
        <div className="absolute -top-14 -right-14 w-40 h-40 rounded-full bg-blue-900 opacity-35" />
        <div className="absolute -bottom-10 -left-10 w-28 h-28 rounded-full bg-blue-900 opacity-25" />

        <div className="z-10">
          <div className="mb-8">
            <NovaLogo />
          </div>

          <nav className="flex flex-col gap-1">
            <NavItem
              active={location.pathname === '/dashboard'}
              path="/dashboard"
              label="Inicio"
              icon="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
            />
            <NavItem
              active={location.pathname === '/history'}
              path="/history"
              label="Movimientos"
              icon="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
            />
            <NavItem
              active={location.pathname === '/statistics'}
              path="/statistics"
              label="Estadísticas"
              icon="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
            />
          </nav>
        </div>

        <div
          onClick={handleLogout}
          className="z-10 flex items-center gap-2 px-3 py-2 rounded-lg cursor-pointer text-slate-600 text-sm hover:text-red-400 hover:bg-slate-800 transition"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Cerrar sesión
        </div>
      </div>

      {/* Main */}
      <div className="flex-1 flex flex-col bg-gray-50">

        {/* Topbar */}
        <div className="bg-white border-b border-gray-100 px-6 py-4 flex justify-between items-center">
          <h1 className="text-base font-medium text-gray-900">
            Hola, {user?.firstName || ''} !
          </h1>
          <p className="text-xs text-gray-400 mt-0.5">{user?.email || '...'}</p>
          <div className="relative flex items-center gap-4" ref={profileRef}>

            {onStartTour && (
              <button
                ref={refHelpBtn}
                onClick={onStartTour}
                className="w-8 h-8 rounded-full bg-blue-50 border border-blue-200 flex items-center justify-center text-blue-500 text-sm font-medium hover:bg-blue-100 transition cursor-pointer"
                title="Ver tour"
              >
                ?
              </button>
            )}

            <div
              onClick={() => setShowProfile(!showProfile)}
              className={`w-8 h-8 rounded-full bg-blue-900 flex items-center justify-center cursor-pointer border-2 transition ${
                showProfile ? 'border-blue-500' : 'border-transparent'
              }`}
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4 text-blue-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>

            {showProfile && (
              <div className="absolute right-0 top-10 w-72 z-50 rounded-2xl overflow-hidden border border-white/15"
                style={{ backdropFilter: 'blur(20px)', background: 'rgba(15, 23, 42, 0.85)' }}>

                <div className="flex items-center gap-3 px-4 py-3 border-b border-white/8">
                  <div className="w-10 h-10 rounded-full flex items-center justify-center"
                    style={{ background: 'rgba(59,130,246,0.25)', border: '1.5px solid rgba(59,130,246,0.5)' }}>
                    <svg className="w-5 h-5 text-blue-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                    </svg>
                  </div>
                  <div>
                    <p className="text-white text-sm font-medium leading-none">{user?.firstName} {user?.lastName}</p>
                    <p className="text-white/40 text-xs mt-1">cuenta personal</p>
                  </div>
                </div>

                <div className="py-2">
                  {[
                    { label: 'nombre', value: `${user?.firstName} ${user?.lastName}` },
                    { label: 'dni', value: user?.dni },
                    { label: 'email', value: user?.email },
                  ].map((item, i, arr) => (
                    <div key={item.label}>
                      <div className="flex items-center gap-3 px-4 py-3">
                        <div className="w-7 h-7 rounded-lg flex items-center justify-center"
                          style={{ background: 'rgba(255,255,255,0.06)' }}>
                          <span className="text-white/40 text-xs">{item.label[0].toUpperCase()}</span>
                        </div>
                        <div>
                          <p className="text-white/35 text-xs leading-none">{item.label}</p>
                          <p className="text-white/85 text-xs font-medium mt-0.5">{item.value}</p>
                        </div>
                      </div>
                      {i < arr.length - 1 && <div className="mx-4 h-px" style={{ background: 'rgba(255,255,255,0.07)' }} />}
                    </div>
                  ))}
                </div>

                <div className="px-4 pb-3">
                  <button onClick={handleLogout}
                    className="flex items-center gap-2 w-full px-3 py-3 rounded-xl text-red-400 text-xs font-medium cursor-pointer"
                    style={{ background: 'rgba(239,68,68,0.12)', border: '0.5px solid rgba(239,68,68,0.25)' }}>
                    <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
                    </svg>
                    Cerrar sesión
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Page content */}
        {children}
      </div>
    </div>
  )
}

export default AppLayout